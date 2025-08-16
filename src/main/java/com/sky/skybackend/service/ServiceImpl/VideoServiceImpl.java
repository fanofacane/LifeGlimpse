package com.sky.skybackend.service.ServiceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.skybackend.Enum.Constant;
import com.sky.skybackend.domain.dto.VideoDTO;
import com.sky.skybackend.domain.pojo.*;
import com.sky.skybackend.domain.vo.VideoVO;
import com.sky.skybackend.mapper.VideoMapper;
import com.sky.skybackend.service.VideoService;
import com.sky.skybackend.utils.CurrentHolder;
import com.sky.skybackend.utils.VideoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

import static com.sky.skybackend.Enum.Constant.DEFAULT_TAGS_COUNT;

@Service
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Autowired
    private SlidingWindowTagScorerService slidingWindowTagScorerService;
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private VideoUtil videoUtil;
    private static final String FEED_INBOX_PREFIX = "feed:inbox:";
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
        video.setReviewStatus(Constant.REVIEW_VIDEOS_STATUS);
        save(video);
        videoMapper.insertTags(video.getId(),videoDTO.getTags());
    }

    @Override
    public List<VideoVO> getMatchVideo(String key, Integer userId) {
        List<VideoVO> videoList=videoMapper.getMatchVideoByKey(key);
        if (CollectionUtils.isEmpty(videoList)) return List.of();
        if (userId != null) videoUtil.FillVideoInfo(userId, videoList);
        return videoList;
    }

    @Override
    public List<VideoVO> getVideoByActionType(Integer userId, int type) {
        List<VideoVO> videoList=videoMapper.getVideoByActionType(userId,type);
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
        Map<String, Object> result  = new HashMap<>();
        result.put("data", feedVideo);
        result.put("lastTimestamp", lastTimestamp1);
        return result;
    }
}
