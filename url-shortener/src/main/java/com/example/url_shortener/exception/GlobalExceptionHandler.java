package com.example.url_shortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortUrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleShortUrlNotFound(
            ShortUrlNotFoundException exception) {

        return Map.of(
                "message", exception.getMessage()
        );
    }
}