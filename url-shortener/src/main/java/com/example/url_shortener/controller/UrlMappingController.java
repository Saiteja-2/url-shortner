package com.example.url_shortener.controller;

import com.example.url_shortener.model.urlMapping;
import com.example.url_shortener.request.UrlCreateRequest;
import com.example.url_shortener.response.UrlCreateResponse;
import com.example.url_shortener.response.UrlMetadataResponse;
import com.example.url_shortener.service.IdempotencyService;
import com.example.url_shortener.service.RateLimitService;
import com.example.url_shortener.service.UrlMappingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@RequiredArgsConstructor
public class UrlMappingController {

    private final UrlMappingService urlMappingService;
    private final RateLimitService rateLimitService;
    private final IdempotencyService idempotencyService;

    @PostMapping
    public ResponseEntity<UrlCreateResponse> createShortUrl(
            @Valid @RequestBody UrlCreateRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            HttpServletRequest httpServletRequest) {

        // 1. Check whether this idempotency key already exists
        String existingValue =
                idempotencyService.getValue(idempotencyKey);

        if (existingValue != null) {

            // Another request with the same key is currently running
            if (existingValue.equals("PROCESSING")) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .build();
            }

            // Request was already completed
            urlMapping mapping =
                    urlMappingService.getByShortCode(existingValue);

            UrlCreateResponse response = new UrlCreateResponse(
                    mapping.getShortCode(),
                    "http://localhost:8080/" + mapping.getShortCode(),
                    mapping.getOriginalUrl()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        }

        // 2. Atomically claim the idempotency key
        boolean started =
                idempotencyService.startProcessing(idempotencyKey);

        if (!started) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        // 3. Rate limiting
        String clientIp =
                httpServletRequest.getRemoteAddr();

        if (!rateLimitService.isAllowed(clientIp)) {

            idempotencyService.delete(idempotencyKey);

            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .build();
        }

        try {

            // 4. Create the short URL
            urlMapping mapping =
                    urlMappingService.createShortUrl(request.url());

            UrlCreateResponse response = new UrlCreateResponse(
                    mapping.getShortCode(),
                    "http://localhost:8080/" + mapping.getShortCode(),
                    mapping.getOriginalUrl()
            );

            // 5. Store short code against idempotency key
            idempotencyService.saveResult(
                    idempotencyKey,
                    mapping.getShortCode()
            );

            // 6. Return response
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (Exception exception) {

            // Allow client to retry if processing failed
            idempotencyService.delete(idempotencyKey);

            throw exception;
        }
    }
    // Get URL metadata
    @GetMapping("/info/{shortCode}")
    public ResponseEntity<UrlMetadataResponse> getUrlMetadata(
            @PathVariable String shortCode) {

        urlMapping mapping =
                urlMappingService.getByShortCode(shortCode);

        UrlMetadataResponse response = new UrlMetadataResponse(
                mapping.getShortCode(),
                mapping.getOriginalUrl(),
                mapping.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteShortUrl(
            @PathVariable String shortCode) {

        urlMappingService.deleteByShortCode(shortCode);

        return ResponseEntity.noContent().build();
    }
}