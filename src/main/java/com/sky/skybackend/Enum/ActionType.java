package com.sky.skybackend.Enum;

public enum ActionType {
    LIKE(1, 3.0, "点赞"),
    COLLECT(2, 5.0, "收藏"),
    VIEW(3, 1.0, "观看"),
    SHARE(4, 4.0, "分享"),
    COMMENT(5, 3.0, "评论"),
    SKIP(6, -1.0, "跳过");

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
}