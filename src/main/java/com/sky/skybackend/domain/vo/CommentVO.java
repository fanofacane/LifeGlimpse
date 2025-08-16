package com.sky.skybackend.domain.vo;

import com.sky.skybackend.domain.pojo.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class CommentVO extends Comment {
    private Integer userId;
    private String nickName;
    private String avatar;
    private Boolean isLike = false;
    private List<CommentVO> replies;
}
