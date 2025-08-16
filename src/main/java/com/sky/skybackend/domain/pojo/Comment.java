package com.sky.skybackend.domain.pojo;

import com.sky.skybackend.domain.dto.CommentDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
public class Comment extends CommentDTO {
        private Integer likeCount = 0;
        private Integer commentCount = 0;
        private LocalDateTime createTime;
}
