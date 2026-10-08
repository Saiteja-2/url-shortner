package com.example.url_shortener.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlCreateRequest(

        @NotBlank(message = "URL cannot be empty")
        @Pattern(
                regexp = "^(https?://).+",
                message = "URL must start with http:// or https://"
        )
        String url

) {
}