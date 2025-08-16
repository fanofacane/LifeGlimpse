package com.sky.skybackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.skybackend.domain.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT nick_name,avatar FROM user WHERE id = #{userId}")
    Map<String,String> getNicknameAvatar(Integer userId);
}
