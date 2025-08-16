package com.sky.skybackend.domain.dto;

import lombok.Data;

import java.util.List;

@Data
public class VideoDTO {
    private Integer id;
    private String title;
    private String description;
    private Integer type;
    private String url;
    private List<Integer> tags;
    private Integer userId;
    private Integer open;
}
