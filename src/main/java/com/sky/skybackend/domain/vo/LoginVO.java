package com.sky.skybackend.domain.vo;

import lombok.Data;

@Data
public class LoginVO extends UserVO{
    private String token;
}
