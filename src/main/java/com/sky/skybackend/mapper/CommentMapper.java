package com.sky.skybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.skybackend.domain.pojo.Comment;
import com.sky.skybackend.domain.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @Update("update comment set comment_count = comment_count + 1 where id = #{parentId}")
    void incrementCount(Integer parentId);
    @Select("SELECT * FROM comment WHERE parent_id = #{parentId}")
    List<CommentVO> getRepliesByParentId(Integer parentId);
    @Select("SELECT * FROM comment WHERE video_id = #{videoId} AND parent_id IS NULL")
    List<CommentVO> getTopLevelComments(Integer videoId);
}
