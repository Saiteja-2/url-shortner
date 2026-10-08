package com.example.url_shortener.service;

import com.example.url_shortener.exception.ShortUrlNotFoundException;
import com.example.url_shortener.model.urlMapping;
import com.example.url_shortener.repository.UrlMappingRepository;
import com.example.url_shortener.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository urlMappingRepository;
    private final StringRedisTemplate redisTemplate;

    public urlMapping createShortUrl(String originalUrl) {

        // 1. Create mapping without short code
        urlMapping mapping = new urlMapping();

        mapping.setOriginalUrl(originalUrl);
        mapping.setCreatedAt(LocalDateTime.now());

        // 2. Save → MySQL generates AUTO_INCREMENT ID
        mapping = urlMappingRepository.save(mapping);

        // 3. Convert database ID to Base62
        String shortCode = Base62Encoder.encode(mapping.getId());

        // 4. Set short code
        mapping.setShortCode(shortCode);

        // 5. Save again → update existing row
        return urlMappingRepository.save(mapping);
    }
    public urlMapping getByShortCode(String shortCode) {

        // 1. Check Redis
        String originalUrl = redisTemplate
                .opsForValue()
                .get(shortCode);

        // 2. Cache HIT
        if (originalUrl != null) {

            urlMapping urlMapping = new urlMapping();
            urlMapping.setShortCode(shortCode);
            urlMapping.setOriginalUrl(originalUrl);

            return urlMapping;
        }

        // 3. Cache MISS → Check MySQL
        urlMapping urlMapping = urlMappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException("Short URL not found"));

        // 4. Store result in Redis
        redisTemplate
                .opsForValue()
                .set(shortCode, urlMapping.getOriginalUrl(),
                        5,
                        TimeUnit.MINUTES);

        // 5. Return result
        return urlMapping;
    }
    public void deleteByShortCode(String shortCode) {

        // Check if URL exists
        urlMappingRepository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new ShortUrlNotFoundException("Short URL not found"));

        // Delete from MySQL
        urlMappingRepository.deleteByShortCode(shortCode);

        // Delete from Redis cache
        redisTemplate.delete(shortCode);
    }
}