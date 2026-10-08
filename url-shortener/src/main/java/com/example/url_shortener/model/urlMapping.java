package com.example.url_shortener.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("url_mapping")

public class urlMapping {

    @Id
    private Long id;

    private String shortCode;

    private String originalUrl;

    private LocalDateTime createdAt;

}
