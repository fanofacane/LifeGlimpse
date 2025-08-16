package com.sky.skybackend.domain.dto;

import lombok.Data;

@Data
public class CommentDTO {
    private Integer id;
    private Integer videoId;
    private Integer userId;
    private Integer parentId;
    private String content;
}
