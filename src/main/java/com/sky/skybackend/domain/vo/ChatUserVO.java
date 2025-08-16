package com.sky.skybackend.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ChatUserVO {
    private Integer id;
    private String nickName;
    private String avatar;
    private String content;
    private Integer unreadCount;
    private LocalDateTime createTime;
}
