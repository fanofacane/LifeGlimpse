package com.sky.skybackend.domain.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserTagOrigin {
    private Integer type;
    private Integer tagId;
    private LocalDateTime createTime;
}
