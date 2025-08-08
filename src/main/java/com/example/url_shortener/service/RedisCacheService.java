package com.example.url_shortener.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getOriginalUrlFromCache(String shortCode) {
        return redisTemplate.opsForValue().get(shortCode);
    }

    public void cacheShortenedUrl(String shortCode, String originalUrl, Long ttlSeconds) {
        redisTemplate.opsForValue().set(shortCode, originalUrl, Duration.ofSeconds(ttlSeconds));
    }
}

