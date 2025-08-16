package com.sky.skybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.skybackend.domain.pojo.Status;
import com.sky.skybackend.domain.pojo.UserAction;
import com.sky.skybackend.domain.pojo.UserTagOrigin;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserActionMapper extends BaseMapper<UserAction> {
        /**
         * 获取用户在滑动窗口内的行为数据（按标签聚合）
         */
        @Select(" SELECT ua.type, v.tag_id , ua.create_time FROM user_action ua JOIN video_tag_relation v ON ua.video_id = v.video_id WHERE ua.user_id = #{userId} AND ua.create_time >= #{windowStart} ")
        List<UserTagOrigin> getUserActionsByWindow(@Param("userId") Integer userId,
                                                   @Param("windowStart") LocalDateTime windowStart);

        /**
         * 清理过期的用户行为数据
         */
        @Select("DELETE FROM user_actions WHERE created_at < #{cutoffTime}")
        int deleteExpiredActions(@Param("cutoffTime") LocalDateTime cutoffTime);

        @Select("SELECT * FROM user_action where user_id=#{userId} and video_id=#{videoId} and type=#{type}")
        UserAction getUserAction(UserAction userAction);
        @Select("SELECT COUNT(1) FROM user_action where comment_id=#{commentId} and user_id=#{userId}")
        Boolean checkLike(Integer commentId, Integer userId);

        @Select("SELECT COUNT(1) FROM user_action where video_id= #{videoId} and user_id= #{userId} and type= #{type}")
        boolean checkStatusExists(Integer userId, Integer videoId, int type);
        @MapKey("id")
        Map<Integer, Status> batchCheckStatusExists(
                @Param("userId") Integer userId,
                @Param("videoIds") List<Integer> videoIds,
                @Param("type") Integer type
        );
}
