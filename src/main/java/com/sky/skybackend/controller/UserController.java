package com.sky.skybackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.sky.skybackend.domain.dto.LoginDTO;
import com.sky.skybackend.domain.pojo.Result;
import com.sky.skybackend.domain.pojo.User;
import com.sky.skybackend.domain.vo.LoginVO;
import com.sky.skybackend.domain.vo.UserVO;
import com.sky.skybackend.service.UserService;
import com.sky.skybackend.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    //查询用户基本信息
    @GetMapping("/getInfo/{id}")
    public Result getUserById(@PathVariable Integer id) {
        return Result.success(BeanUtil.copyProperties(userService.getById(id), UserVO.class));
    }
    //修改用户信息
    @PostMapping("/updateInfo")
    public Result updateUserInfo(@RequestBody User user) {
        if (!userService.updateById(user)) return Result.error("修改失败");
        return Result.success("修改成功");
    }
    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO){
        User user = userService.lambdaQuery()
                .eq(User::getEmail, loginDTO.getEmail())
                .one();
        if (user == null) return Result.error("用户不存在");
        if (!user.getPassword().equals(loginDTO.getPassword())) return Result.error("密码错误");
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        LoginVO loginVO = BeanUtil.copyProperties(user, LoginVO.class);
        loginVO.setToken(JwtUtils.generateToken(map));
        return Result.success(loginVO);
    }
    /*
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody LoginDTO loginDTO) {
        if (userService.lambdaQuery()
                .eq(User::getEmail, loginDTO.getEmail())
                .one() != null) return Result.error("用户已存在");
        return Result.success(userService.save(BeanUtil.copyProperties(loginDTO, User.class)));
    }
}
