package com.example.url_shortener.response;

public record UrlCreateResponse(
        String shortCode,
        String shortUrl,
        String originalUrl
) {
}