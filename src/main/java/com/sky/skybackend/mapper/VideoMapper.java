package com.sky.skybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.pojo.Video;
import com.sky.skybackend.domain.vo.VideoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    void updateVideoCount(Integer videoId, Integer type, Integer count);

    @Update("update video set comment_count = comment_count + 1 where id = #{videoId}")
    void increCommentCount(Integer videoId);

    @Select("select id from tags order by RAND() limit 5")
    List<Integer> getRandomTags();

    void insertTags(Integer videoId,List<Integer> tags);

    List<VideoVO> queryMoreVideo(Integer cursorId, LocalDateTime cursorTime, List<Integer> tags, int size);


    List<VideoVO> getMatchVideoByKey(String key);

    List<VideoVO> getVideoByActionType(Integer userId, int type);

    List<VideoVO> getVideoByFollowIds(List<Integer> followedIds);

    List<VideoVO> getVideoByVideoIds(List<Integer> videoIds);
}
