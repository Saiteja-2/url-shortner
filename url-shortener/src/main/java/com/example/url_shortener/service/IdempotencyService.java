package com.example.url_shortener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    private static final String PROCESSING = "PROCESSING";
    private static final long TTL_MINUTES = 10;

    private String buildKey(String idempotencyKey) {
        return "idempotency:" + idempotencyKey;
    }

    // Try to claim this idempotency key
    public boolean startProcessing(String idempotencyKey) {

        return Boolean.TRUE.equals(
                redisTemplate.opsForValue().setIfAbsent(
                        buildKey(idempotencyKey),
                        PROCESSING,
                        TTL_MINUTES,
                        TimeUnit.MINUTES
                )
        );
    }

    // Get existing value
    public String getValue(String idempotencyKey) {

        return redisTemplate
                .opsForValue()
                .get(buildKey(idempotencyKey));
    }

    // Save the final short code
    public void saveResult(
            String idempotencyKey,
            String shortCode) {

        redisTemplate.opsForValue().set(
                buildKey(idempotencyKey),
                shortCode,
                TTL_MINUTES,
                TimeUnit.MINUTES
        );
    }

    // Remove key if processing fails
    public void delete(String idempotencyKey) {

        redisTemplate.delete(buildKey(idempotencyKey));
    }
}