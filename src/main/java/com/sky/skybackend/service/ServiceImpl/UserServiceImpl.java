package com.sky.skybackend.service.ServiceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.skybackend.domain.pojo.User;
import com.sky.skybackend.mapper.UserMapper;
import com.sky.skybackend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
