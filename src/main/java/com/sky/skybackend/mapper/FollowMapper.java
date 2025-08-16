package com.sky.skybackend.mapper;

import com.sky.skybackend.domain.pojo.Status;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FollowMapper{
    @Select("SELECT COUNT(*) FROM follow WHERE user_id = #{userId} AND follow_id = #{followId}")
    boolean checkIsFollow(Integer userId, Integer followId);

    @MapKey("id")
    Map<Integer, Status> batchCheckIsFollow(
            @Param("userId") Integer userId,
            @Param("followedUserIds") List<Integer> followedUserIds
    );
    List<Integer> getFollowIdList(Integer userId);
}
