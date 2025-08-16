package com.sky.skybackend.domain.pojo;

import lombok.Data;

@Data
public class Status {
    private Integer id; // 用于 @MapKey
    private Boolean status;     // 实际的布尔状态
}
