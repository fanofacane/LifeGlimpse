package com.sky.skybackend.domain.pojo;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketConnectionManager {
    // 线程安全的Map，存储用户ID与Channel的映射
    private static final Map<String, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();

    // 将用户ID与Channel关联并存储
    public static void addConnection(String userId, Channel channel) {
        USER_CHANNEL_MAP.put(userId, channel);
    }

    // 根据用户ID获取对应的Channel
    public static Channel getChannelByUserId(String userId) {
        return USER_CHANNEL_MAP.get(userId);
    }

    // 移除用户连接
    public static void removeConnection(String userId) {
        USER_CHANNEL_MAP.remove(userId);
    }
    //连接数量
    public static int getConnectionCount() {
        return USER_CHANNEL_MAP.size();
    }
}
