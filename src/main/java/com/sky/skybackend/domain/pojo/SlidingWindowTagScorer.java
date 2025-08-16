package com.sky.skybackend.domain.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowTagScorer {
    private final String userId;           // 用户ID
    private final int windowDays;          // 滑动窗口天数（比如30天）
    private final double decayFactor;      // 时间衰减因子（比如0.95）
    private final List<UserAction> actionHistory;  // 用户行为历史记录
    private final Map<String, Double> tagScores;   // 每个标签的得分

    public SlidingWindowTagScorer(String userId, int windowDays, double decayFactor) {
        this.userId = userId;
        this.windowDays = windowDays;      // 比如设置为30，表示只看最近30天的行为
        this.decayFactor = decayFactor;    // 比如0.95，表示每过一天权重乘以0.95
        this.actionHistory = new ArrayList<>();
        this.tagScores = new ConcurrentHashMap<>();
    }
}

