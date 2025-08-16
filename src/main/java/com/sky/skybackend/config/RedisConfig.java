package com.sky.skybackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 实例化RedisTemplate并指定泛型
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 配置序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();

        // 设置key序列化器
        redisTemplate.setKeySerializer(stringSerializer);
        // 设置value序列化器
        redisTemplate.setValueSerializer(jsonSerializer);
        // 设置hash key序列化器
        redisTemplate.setHashKeySerializer(stringSerializer);
        // 设置hash value序列化器
        redisTemplate.setHashValueSerializer(jsonSerializer);

        // 初始化参数设置
        redisTemplate.afterPropertiesSet();

        log.info("RedisTemplate配置完成");
        return redisTemplate;
    }
}
