package com.sky.skybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.skybackend.domain.pojo.UserTagScore;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserTagScoreMapper extends BaseMapper<UserTagScore> {

    /**
     * 获取用户的标签偏好得分
     */
    @Select("""
        SELECT 
            uts.user_id,
            uts.tag_id,
            t.tag_name,
            uts.raw_score,
            uts.normalized_score,
            uts.action_count,
            uts.last_action_time
        FROM user_tag_scores uts
        JOIN tags t ON uts.tag_id = t.tag_id
        WHERE uts.user_id = #{userId}
          AND uts.normalized_score > 0
        ORDER BY uts.normalized_score DESC
        LIMIT #{limit}
    """)
    List<Map<String, Object>> getUserTagPreferences(@Param("userId") String userId,
                                                    @Param("limit") int limit);


    void batchInsert(List<UserTagScore> tagScore);
}
