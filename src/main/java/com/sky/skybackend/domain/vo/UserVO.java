package com.sky.skybackend.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class UserVO {
    private Integer id;
    private String nickName;
    private String description;
    private Integer sex;
    private Integer age;
    private String avatar;
    private String ip;
    private Integer fansCount;
    private Integer followCount;
    private Integer likeCount;
}
