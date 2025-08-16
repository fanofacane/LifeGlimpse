package com.sky.skybackend.domain.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserTagScore {
    private Long id;
    private Integer userId;
    private Integer tagId;
    private BigDecimal rawScore;
    private BigDecimal normalizedScore;
    private LocalDateTime windowStartTime;
    private LocalDateTime windowEndTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    public UserTagScore() {}

    public UserTagScore(Integer userId, Integer tagId, BigDecimal rawScore, BigDecimal normalizedScore) {
        this.userId = userId;
        this.tagId = tagId;
        this.rawScore = rawScore;
        this.normalizedScore = normalizedScore;
        this.windowStartTime = LocalDateTime.now().minusDays(30);
        this.windowEndTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
}
