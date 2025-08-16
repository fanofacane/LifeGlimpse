package com.sky.skybackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sky.skybackend.domain.dto.CommentDTO;
import com.sky.skybackend.domain.dto.VideoDTO;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.pojo.Video;
import com.sky.skybackend.domain.vo.CommentVO;
import com.sky.skybackend.domain.vo.VideoVO;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface VideoService extends IService<Video> {
    

    Map<String, Object> queryMoreVideo(Integer userId, Integer cursor, int size);


    void insert(VideoDTO videoDTO);

    List<VideoVO> getMatchVideo(String key, Integer userId);

    List<VideoVO> getVideoByActionType(Integer userId, int type);

    Map<String, Object> getInboxVideos(Integer count, Long lastTimestamp);
}
