package com.example.url_shortener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SECONDS = 60;

    public boolean isAllowed(String clientIp) {

        String key = "rate_limit:" + clientIp;

        Long requestCount = redisTemplate
                .opsForValue()
                .increment(key);

        if (requestCount != null && requestCount == 1) {
            redisTemplate.expire(
                    key,
                    WINDOW_SECONDS,
                    TimeUnit.SECONDS
            );
        }

        return requestCount != null &&
                requestCount <= MAX_REQUESTS;
    }
}