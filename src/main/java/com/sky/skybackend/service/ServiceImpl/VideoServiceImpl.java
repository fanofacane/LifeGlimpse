package com.sky.skybackend.service.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.skybackend.Enum.Constant;
import com.sky.skybackend.domain.dto.VideoDTO;
import com.sky.skybackend.domain.pojo.*;
import com.sky.skybackend.domain.vo.HotVideo;
import com.sky.skybackend.domain.vo.VideoVO;
import com.sky.skybackend.mapper.VideoMapper;
import com.sky.skybackend.service.VideoService;
import com.sky.skybackend.utils.AliyunVideoAuditTool;
import com.sky.skybackend.utils.CurrentHolder;
import com.sky.skybackend.utils.RedisCacheUtil;
import com.sky.skybackend.utils.VideoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

import static com.sky.skybackend.Enum.Constant.*;

@Service
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private SlidingWindowTagScorerService slidingWindowTagScorerService;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoUtil videoUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Override
    public Map<String, Object> queryMoreVideo(Integer userId, Integer cursor, int size) {
        /*
         * 获取标签
         * 游客登录随机抽取DEFAULT_TAGS_COUNT个标签
         */
        // 1. 获取标签
        List<Integer> tags = (userId != null) ?
                slidingWindowTagScorerService.getTopNTagsFromRedis(userId, DEFAULT_TAGS_COUNT) :
                videoMapper.getRandomTags();
        if (CollectionUtils.isEmpty(tags)) tags = videoMapper.getRandomTags();
        // 2. 根据游标获取视频列表
        LocalDateTime cursorTime = null;
        if (cursor != null) { // 只有当cursor存在时才去查询video
            Video cursorVideo = getById(cursor);
            if (cursorVideo != null) cursorTime = cursorVideo.getCreateTime();
        }
        List<VideoVO> videoList = videoMapper.queryMoreVideo(cursor, cursorTime, tags, size);
        log.info("查询到的视频列表数量: {}", videoList.size());
        // 3. 处理空列表返回
        if (CollectionUtils.isEmpty(videoList)) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("data", Collections.emptyList()); // 返回空列表
            emptyResponse.put("nextCursor", null);
            return emptyResponse;
        }
        // 4. 计算下一次游标
        Integer nextCursor = videoList.getLast().getId();
        // 5. 填充所有视频信息
        videoUtil.FillVideoInfo(userId, videoList);
        // 打乱列表
        Collections.shuffle(videoList);
        // 返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("data", videoList);
        response.put("nextCursor", nextCursor);
        return response;
    }


    @Override
    public void insert(VideoDTO videoDTO) {
        Video video = BeanUtil.copyProperties(videoDTO, Video.class);
        video.setReviewStatus(REVIEW_VIDEOS_STATUS);
        save(video);
        Thread asyncThread = new Thread(() -> {
            try {
                videoAudit(video.getId(),video.getUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        asyncThread.start(); // 启动异步线程
        videoMapper.insertTags(video.getId(), videoDTO.getTags());
    }

    @Override
    public List<VideoVO> getMatchVideo(String key, Integer userId) {
        List<VideoVO> videoList = videoMapper.getMatchVideoByKey(key);
        if (CollectionUtils.isEmpty(videoList)) return List.of();
        if (userId != null) videoUtil.FillVideoInfo(userId, videoList);
        return videoList;
    }

    @Override
    public List<VideoVO> getVideoByActionType(Integer userId, int type) {
        List<VideoVO> videoList = videoMapper.getVideoByActionType(userId, type);
        if (CollectionUtils.isEmpty(videoList)) return List.of();
        videoUtil.FillVideoInfo(userId, videoList);
        return videoList;
    }

    @Override
    public Map<String, Object> getInboxVideos(Integer count, Long lastTimestamp) {
        Integer userId = CurrentHolder.getCurrentId();
        String inboxKey = FEED_INBOX_PREFIX + userId;
        List<Integer> videosIds = videoUtil.getUserInboxVideosIds(userId, count, lastTimestamp);
        Long lastTimestamp1 = videoUtil.getLastTimestamp(videosIds, inboxKey);
        List<VideoVO> feedVideo = videoUtil.getFeedVideo(videosIds);
        Map<String, Object> result = new HashMap<>();
        result.put("data", feedVideo);
        result.put("lastTimestamp", lastTimestamp1);
        return result;
    }

    @Override
    public List<HotVideo> getHotRank() {
        Set<ZSetOperations.TypedTuple<String>> zSet = redisTemplate.opsForZSet().reverseRangeWithScores(REDIS_HOT_RANK_KEY, 0, -1);
        List<HotVideo> hotVideos = new ArrayList<>();
        for (ZSetOperations.TypedTuple<String> tuple : zSet) {
            final HotVideo hotVideo;
            try {
                hotVideo = objectMapper.readValue(tuple.getValue().toString(), HotVideo.class);
                hotVideo.setHot(tuple.getScore());
                hotVideo.hotFormat();
                hotVideos.add(hotVideo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hotVideos;
    }

    @Override
    public List<VideoVO> getHotVideos() {
        Integer userId = CurrentHolder.getCurrentId();
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DATE);
        HashMap<String, Integer> map = new HashMap<>();
        map.put(HOT_VIDEO + today, 12);
        map.put(HOT_VIDEO + (today - 1), 5);
        map.put(HOT_VIDEO + (today - 2), 3);
        List<Integer> hotVideoIds = redisCacheUtil.pipeline(connection -> {
            map.forEach((k, v) -> {
                connection.sRandMember(k.getBytes(), v);
            });
            return null;
        });
        List<Integer> videoIds = new ArrayList<>();
        for (Object ids : hotVideoIds) {
            videoIds.addAll((List) ids);
        }
        List<VideoVO> videos = videoMapper.getVideoByVideoIds(videoIds);
        videoUtil.FillVideoInfo(userId, videos);
        return videoIds.isEmpty() ? List.of() : videos;
    }

    public void videoAudit(Integer videoId,String url) {
        try {
            // 1. 初始化（实际项目中建议在启动类初始化一次）
            String regionId = "cn-shanghai"; // 替换为你的地域
            String accessKeyId = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"); // 从环境变量获取
            String accessKeySecret = System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"); // 从环境变量获取
            AliyunVideoAuditTool.init(regionId, accessKeyId, accessKeySecret);
            // 2. 调用审核（传入视频URL）
            JSONObject auditResult = AliyunVideoAuditTool.auditVideo(url);

            // 3. 业务解析
            // 1. 先判断任务是否成功完成
            if (!"FINISHED".equals(auditResult.getString("status")) || auditResult.getIntValue("code") != 200) {
                System.out.println("审核任务未正常完成：" + auditResult.getString("msg"));
                return;
            }
            // 2. 解析色情场景结果（判断是否违规）
            boolean isPornViolation = false;
            JSONArray pornArray = auditResult.getJSONArray("porn");
            for (int i = 0; i < pornArray.size(); i++) {
                JSONObject pornFrame = pornArray.getJSONObject(i);
                String label = pornFrame.getString("label");
                // 若存在色情/性感/低俗标签，判定为违规
                if ("porn".equals(label) || "sexy".equals(label) || "vulgar".equals(label)) {
                    isPornViolation = true;
                    System.out.println("色情违规帧：" + pornFrame.getString("url"));
                    System.out.println("违规时间点：" + pornFrame.getLongValue("timestamp") + "毫秒");
                    break; // 只要有一帧违规，即可判定视频违规
                }
            }

            // 3. 解析暴恐场景结果（逻辑同色情）
            boolean isTerrorViolation = false;
            JSONArray terrorismArray = auditResult.getJSONArray("terrorism");
            for (int i = 0; i < terrorismArray.size(); i++) {
                JSONObject terrorFrame = terrorismArray.getJSONObject(i);
                if ("terrorism".equals(terrorFrame.getString("label"))) {
                    isTerrorViolation = true;
                    System.out.println("暴恐违规帧：" + terrorFrame.getString("url"));
                    break;
                }
            }

            // 4. 业务决策：有任一违规则拦截，否则通过
            if (isPornViolation || isTerrorViolation) {
                System.out.println("视频存在违规内容，执行拦截逻辑");
                // 此处添加你的业务逻辑：如删除视频、拒绝发布、发送通知等
            } else {
                System.out.println("视频审核通过，执行发布逻辑");
            }
        }catch (Exception e){
            System.out.println("视频审核失败");
        }
    }
}