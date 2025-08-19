package com.sky.skybackend.domain.pojo;

import com.sky.skybackend.domain.dto.VideoDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class Video {
    private Integer id;
    private String title;
    private String description;
    private Integer type;
    private String url;
    private Integer userId;
    private Integer open;
    private Integer reviewStatus;
    private String reviewContent;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private Integer watchCount;
    private Integer shareCount;
    private LocalDateTime createTime;
}
