package com.yulin.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置缓存过期时间
                .entryTtl(Duration.ofMinutes(30))
                // 设置键的序列化方式
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置值的序列化方式
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 禁用空值缓存
                .disableCachingNullValues();

        // 创建不同的缓存配置
        RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("products", config)
                .withCacheConfiguration("product", config.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration("searchResults", config.entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration("topSelling", config.entryTtl(Duration.ofHours(2)))
                .withCacheConfiguration("cart", config.entryTtl(Duration.ofDays(7)));

        return builder.build();
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置键的序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        
        // 设置值的序列化方式
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        // 设置哈希键的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // 设置哈希值的序列化方式
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
