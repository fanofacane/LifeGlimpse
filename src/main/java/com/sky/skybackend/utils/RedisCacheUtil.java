package com.sky.skybackend.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisCacheUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Pipeline批量操作
     * @return 执行结果列表
     */
    public List pipeline(RedisCallback redisCallback){
        return redisTemplate.executePipelined(redisCallback);
    }

    /**
     * Pipeline回调接口
     */
    @FunctionalInterface
    public interface PipelineCallback<T> {
        T execute(RedisConnection connection) throws Exception;
    }
}

