package com.sky.skybackend.utils;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.sky.skybackend.Enum.ActionType;
import com.sky.skybackend.domain.pojo.Status;
import com.sky.skybackend.domain.pojo.User;
import com.sky.skybackend.domain.vo.VideoVO;
import com.sky.skybackend.mapper.FollowMapper;
import com.sky.skybackend.mapper.UserActionMapper;
import com.sky.skybackend.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class VideoUtil {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private UserActionMapper userActionMapper;
    @Autowired
    private FollowMapper followMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    // Redis键前缀，用于区分不同用户的收件箱
    private static final String FEED_INBOX_PREFIX = "feed:inbox:";
    public List<Integer> getUserInboxVideosIds(Integer userId, Integer count, Long lastTimestamp) {
        String inboxKey = FEED_INBOX_PREFIX + userId;
        Set<String> videoIdStrs;
        if (lastTimestamp == null) {
            // 获取最新的count条视频
            // rangeWithScores方法参数：start=0, end=count-1 表示取前count条
            // 因为是按分数倒序，所以0是最新的
            videoIdStrs = redisTemplate.opsForZSet().reverseRange(inboxKey, 0, count - 1);
        } else {
            // 分页查询，获取比lastTimestamp更早的视频
            videoIdStrs = redisTemplate.opsForZSet().reverseRangeByScore(inboxKey, 0, lastTimestamp - 1, 0, count);
        }
        if (videoIdStrs == null || videoIdStrs.isEmpty()) return List.of();
        // 转换为Long类型的视频ID列表
        return  videoIdStrs.stream()
                .map(Integer::parseInt)
                .toList();
    }
    public Long getLastTimestamp(List<Integer> videoIds, String inboxKey) {
        if (videoIds.isEmpty()) {
            return null;
        }
        Integer lastVideoIdStr = new ArrayList<>(videoIds).get(videoIds.size() - 1);
        Double score = redisTemplate.opsForZSet().score(inboxKey, lastVideoIdStr.toString());
        return score != null ? score.longValue() : null;
    }
    public List<VideoVO> getFeedVideo(List<Integer> videoIds) {
        Integer userId = CurrentHolder.getCurrentId();
        List<VideoVO> videoList = null;
        if (videoIds.isEmpty()){
            List<Integer> followedIds = followMapper.getFollowIdList(userId);
            if (followedIds == null || followedIds.isEmpty()) return List.of();
            videoList = videoMapper.getVideoByFollowIds(followedIds);
        }else {
            videoList = videoMapper.getVideoByVideoIds(videoIds);
        }
        if (videoList == null || videoList.isEmpty()) return List.of();
        FillVideoInfo(userId, videoList);
        return videoList;
    }
    public void FillVideoInfo(Integer userId, List<VideoVO> videoList) {
        List<Integer> videoIds = videoList.stream().map(VideoVO::getId).collect(Collectors.toList());
        Set<Integer> videoPublisherIds = videoList.stream().map(VideoVO::getUserId).collect(Collectors.toSet());
        log.info("待处理视频发布者ID集合大小: {}", videoPublisherIds.size());
        // 6. 批量查询用户信息 - 提取到 if 外部，总是需要填充用户信息
        List<User> users = Db.lambdaQuery(User.class).in(User::getId, videoPublisherIds).list();
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
        // 7. 填充视频信息 - 统一循环
        Map<Integer, Status> likedStatusMap = null;
        Map<Integer, Status> collectedStatusMap = null;
        Map<Integer, Status> followedStatusMap = null;
        if (userId != null) {
            // 批量查询喜欢状态
            likedStatusMap = userActionMapper.batchCheckStatusExists(userId, videoIds, ActionType.LIKE.getCode());
            // 批量查询收藏状态
            collectedStatusMap = userActionMapper.batchCheckStatusExists(userId, videoIds, ActionType.COLLECT.getCode());
            // 批量查询关注状态
            followedStatusMap = followMapper.batchCheckIsFollow(userId, new ArrayList<>(videoPublisherIds));
        }
        for (VideoVO videoVO : videoList) {
            // 统一处理用户状态和用户信息
            // 只有当 userId 不为 null 时才去尝试获取状态，否则 getStatusOrDefault 会直接返回 false
            videoVO.setIsLike(getStatusOrDefault(likedStatusMap, videoVO.getId(), false));
            videoVO.setIsCollect(getStatusOrDefault(collectedStatusMap, videoVO.getId(), false));
            videoVO.setIsFollow(getStatusOrDefault(followedStatusMap, videoVO.getUserId(), false));

            User user = userMap.get(videoVO.getUserId());
            if (user != null) {
                videoVO.setNickName(user.getNickName());
                videoVO.setAvatar(user.getAvatar());
            }
        }
    }

    /**
     * 从Map中获取Status对象并提取其状态，如果Map中不存在或Status对象为空则返回默认值
     * @param map 存储状态的Map，键是ID，值是Status对象
     * @param id 要查询的ID (videoId 或 userId)
     * @param defaultValue 默认返回的布尔值，通常是false
     * @return 对应的布尔状态，如果不存在则返回defaultValue
     */
    private boolean getStatusOrDefault(Map<Integer, Status> map, Integer id, boolean defaultValue) {
        // 使用 Optional 来优雅地处理空指针
        return Optional.ofNullable(map) // Map本身可能为null（尽管通常不会）
                .map(m -> m.get(id)) // 获取Status对象
                .map(Status::getStatus) // 获取Status对象中的布尔状态
                .orElse(defaultValue); // 如果任何一步是null，则返回默认值
    }
}
