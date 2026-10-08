package com.example.url_shortener.repository;

import com.example.url_shortener.model.urlMapping;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UrlMappingRepository extends CrudRepository<urlMapping, Long> {
    Optional<urlMapping> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    long deleteByShortCode(String shortCode);
}
