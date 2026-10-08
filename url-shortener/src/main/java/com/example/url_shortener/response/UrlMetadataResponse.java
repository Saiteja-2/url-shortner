package com.example.url_shortener.response;

import java.time.LocalDateTime;

public record UrlMetadataResponse(
        String shortCode,
        String originalUrl,
        LocalDateTime createdAt
) {
}
