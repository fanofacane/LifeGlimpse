package com.sky.skybackend.domain.pojo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Message implements Serializable {
    private Integer id;
    private String from;
    private String to;
    private String content;
    private LocalDateTime createTime;
}
