package com.sky.skybackend.Enum;

import com.sky.skybackend.domain.pojo.Video;

import java.util.Map;
import java.util.function.Function;

public enum ActionType {
    LIKE(1, 0.5, "点赞"),
    COLLECT(2, 1.0, "收藏"),
    WATCH(3, 0.2, "观看"),
    SHARE(4, 1.0, "分享"),
    COMMENT(5, 0.5, "评论"),
    SKIP(6, -0.2, "跳过");

    private final int code;
    private final double weight;
    private final String description;

    ActionType(int code, double weight, String description) {
        this.code = code;
        this.weight = weight;
        this.description = description;
    }

    public int getCode() { return code; }
    public double getWeight() { return weight; }
    public String getDescription() { return description; }

    public static ActionType fromCode(int code) {
        for (ActionType type : ActionType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的行为类型码: " + code);
    }
    // 定义 count 获取器映射
    public static Map<ActionType, Function<Video, Integer>> countGetters = Map.of(
            SHARE, Video::getShareCount,
            WATCH, Video::getWatchCount,
            LIKE, Video::getLikeCount,
            COLLECT, Video::getCollectCount,
            COMMENT, Video::getCommentCount
    );
}