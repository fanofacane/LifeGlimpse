package com.sky.skybackend.service.ServiceImpl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sky.skybackend.Enum.ActionType;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.pojo.UserTagOrigin;
import com.sky.skybackend.domain.pojo.UserTagScore;
import com.sky.skybackend.mapper.UserActionMapper;
import com.sky.skybackend.mapper.UserTagScoreMapper;
import com.sky.skybackend.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SlidingWindowTagScorerService {
    @Autowired
    private UserActionMapper userActionMapper;
    @Autowired
    private UserTagScoreMapper userTagScoreMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private VideoMapper videoMapper;

    private static final String REDIS_KEY_PREFIX = "user_tag_scores:";

    @Transactional
    public void recordUserAction(UserAction userAction) {
        try {
            //根据用户ID和视频ID和type查询用户行为数据
            UserAction action = userActionMapper.getUserAction(userAction);
            if (action == null){
                //1.不存在新增
                log.info("用户行为不存在，新增："+userAction);
                userActionMapper.insert(userAction);
                videoMapper.updateVideoCount(userAction.getVideoId(), userAction.getType(),1);
                // 2. 异步更新用户标签得分（避免阻塞用户操作）
                updateUserTagScoresAsync(userAction.getUserId());
            }else {
                //1.存在删除
                log.info("用户行为存在，删除："+action);
                userActionMapper.deleteById(action);
                videoMapper.updateVideoCount(userAction.getVideoId(), userAction.getType(),-1);
            }
        } catch (Exception e) {
            log.error("记录用户行为失败"+e);
            throw e;
        }
    }

    /**
     * 异步更新用户标签得分
     * 何时执行：用户行为发生后立即执行，或定时批量执行
     */
    public void updateUserTagScoresAsync(Integer userId) {
        // 用线程池或消息队列处理
        new Thread(() -> updateUserTagScores(userId)).start();
    }

    /**
     * 更新用户标签得分（核心计算逻辑）
     * 何时执行：
     * 1. 用户行为发生后
     * 2. 定时任务批量更新
     * 3. 推荐时实时计算
     */
    @Transactional
    public void updateUserTagScores(Integer userId) {
        log.info("开始更新用户标签得分: userId={}", userId);

        try {
            // 1. 计算窗口时间范围
            LocalDateTime windowEnd = LocalDateTime.now();
            LocalDateTime windowStart = windowEnd.minusDays(30);

            // 2. 获取用户在滑动窗口内的行为数据
            List<UserTagOrigin> userActions = userActionMapper.getUserActionsByWindow(userId, windowStart);

            if (userActions.isEmpty()) {
                log.info("用户在窗口期内无行为数据: userId={}", userId);
                return;
            }

            // 3. 按标签聚合计算得分
            Map<Integer, Double> tagScores = calculateTagScores(userActions);

            // 4. 归一化处理
            Map<Integer, Double> normalizedScores = normalizeScores(tagScores);

            // 5. 保存到数据库
            saveTagScoresToDatabase(userId, normalizedScores, windowStart, windowEnd);

            // 6. 缓存到Redis（提高查询效率）
            cacheTagScoresToRedis(userId, normalizedScores);

            log.info("用户标签得分更新完成: userId={}, 标签数量={}", userId, normalizedScores.size());

        } catch (Exception e) {
            log.error("更新用户标签得分失败: userId={}", userId, e);
        }
    }

    /**
     * 计算标签得分（应用滑动窗口和时间衰减）
     */
    private Map<Integer, Double> calculateTagScores(List<UserTagOrigin> userActions) {
        Map<Integer, Double> tagScores = new HashMap<>();

        for (UserTagOrigin action : userActions) {
            Integer tag = action.getTagId();

            Integer actionTypeCode = action.getType();
            LocalDateTime createTime = action.getCreateTime();
            LocalDateTime now = LocalDateTime.now();
            long daysAgo = ChronoUnit.DAYS.between(createTime, now);

            // 获取行为权重
            ActionType actionType = ActionType.fromCode(actionTypeCode);
            double actionWeight = actionType.getWeight();

            // 计算时间衰减权重
            double timeWeight = Math.pow(0.95, daysAgo);

            // 计算总权重
            double totalWeight = actionWeight * timeWeight;

            // 累加到标签得分
            tagScores.merge(tag, totalWeight, Double::sum);

        }

        return tagScores;
    }

    /**
     * 归一化得分（将得分范围调整到0-1之间）
     */
    private Map<Integer, Double> normalizeScores(Map<Integer, Double> rawScores) {
        if (rawScores == null || rawScores.isEmpty()) {
            return new HashMap<>();
        }

        // 找出最高得分
        double maxScore = rawScores.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1.0);
        // 如果最大得分为0，则返回所有得分为0
        if (maxScore == 0) {
            return rawScores.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> 0.0));
        }

        // 归一化处理
        Map<Integer, Double> normalizedScores = new HashMap<>();
        for (Map.Entry<Integer, Double> entry : rawScores.entrySet()) {
            double normalizedScore = entry.getValue() / maxScore;
            normalizedScores.put(entry.getKey(), normalizedScore);
        }

        return normalizedScores;
    }

    /**
     * 保存标签得分到数据库
     */
    private void saveTagScoresToDatabase(Integer userId, Map<Integer, Double> normalizedScores,
                                         LocalDateTime windowStart, LocalDateTime windowEnd) {

        // 先删除用户的旧得分记录
        userTagScoreMapper.delete(new QueryWrapper<UserTagScore>().eq("user_id", userId));
        log.info("插入用户得分记录: userId={}", userId);
        // 插入新的得分记录
        List<UserTagScore> tagScores = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : normalizedScores.entrySet()) {
            BigDecimal score = BigDecimal.valueOf(entry.getValue());
            UserTagScore tagScore = new UserTagScore(
                    userId,
                    entry.getKey(),
                    score,
                    score.setScale(4, RoundingMode.HALF_UP)
            );
            tagScore.setWindowStartTime(windowStart);
            tagScore.setWindowEndTime(windowEnd);

            tagScores.add(tagScore);
        }
        userTagScoreMapper.batchInsert(tagScores);
    }

    /**
     * 缓存标签得分到Redis
     */
    private void cacheTagScoresToRedis(Integer userId, Map<Integer, Double> normalizedScores) {
        String redisKey = REDIS_KEY_PREFIX + userId;

        try {
            // 遍历标签和得分
            for (Map.Entry<Integer, Double> entry : normalizedScores.entrySet()) {
                String tag = String.valueOf(entry.getKey());
                double score = entry.getValue();
                redisTemplate.opsForZSet().add(redisKey, tag, score);
            }
        } catch (Exception e) {
            System.out.println("缓存异常");
        }
    }

    /**
     * 从Redis获取用户标签得分
     * 何时执行：推荐视频时优先从缓存获取
     */
    public List<Integer> getTopNTagsFromRedis(Integer userId, int topN) {
        String redisKey = REDIS_KEY_PREFIX + userId;
        List<Integer> topTags = new ArrayList<>();

        try {
            // 获取得分最高的前 N 个标签
            Set<String> rawTags = redisTemplate.opsForZSet().reverseRange(redisKey, 0, topN - 1);

            // 确保标签集合不为空，并添加到结果列表
            if (rawTags != null) {
                topTags = rawTags.stream().map(Integer::parseInt).toList();
            }
        } catch (Exception e) {
            System.out.println("获取标签异常");
        }

        return topTags;
    }

}
