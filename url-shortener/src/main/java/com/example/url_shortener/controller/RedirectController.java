package com.example.url_shortener.controller;

import com.example.url_shortener.model.urlMapping;
import com.example.url_shortener.service.UrlMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlMappingService urlMappingService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        urlMapping mapping =
                urlMappingService.getByShortCode(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", mapping.getOriginalUrl())
                .build();
    }
}