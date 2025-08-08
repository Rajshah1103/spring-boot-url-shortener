package com.example.url_shortener.service.impl;

import com.example.url_shortener.dto.UrlRequest;
import com.example.url_shortener.dto.UrlResponse;
import com.example.url_shortener.model.Url;
import com.example.url_shortener.repository.UrlRepository;
import com.example.url_shortener.service.RedisCacheService;
import com.example.url_shortener.service.UrlShortenerService;
import com.example.url_shortener.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlRepository urlRepository;

    @Autowired
    private RedisCacheService redisCacheService;

    @Value("${app.shortener.base-url:http://localhost:8080/}")
    private String baseUrl;

    public UrlShortenerServiceImpl (UrlRepository repository) {
        this.urlRepository = repository;
    }

    @Override
    @Transactional
    public UrlResponse shortenUrl(UrlRequest request) {
        // Check if already exists in DB
        Optional<Url> existing = urlRepository.findByOriginalUrl(request.getOriginalUrl());
        if (existing.isPresent()) {
            return UrlResponse.builder()
                    .shortUrl(baseUrl + existing.get().getShortCode())
                    .originalUrl(existing.get().getOriginalUrl())
                    .build();
        }

        // Step 1: Save the URL entity without shortCode
        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .ttlInSeconds(request.getTtlSeconds())
                .build();

        Url saved = urlRepository.save(url);

        // Step 2: Generate shortCode using the generated ID
        String shortCode = Base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);

        // Step 3: Save again to persist shortCode
        urlRepository.save(saved);

        // Step 4: Cache in Redis if TTL is provided
        if (saved.getTtlInSeconds() != null) {
            redisCacheService.cacheShortenedUrl(shortCode, saved.getOriginalUrl(), saved.getTtlInSeconds());
        }

        return UrlResponse.builder()
                .shortUrl(baseUrl + shortCode)
                .originalUrl(saved.getOriginalUrl())
                .build();
    }



    @Override
    public String getOriginalUrl(String shortCode) {
        // Try Redis first
        String cached = redisCacheService.getOriginalUrlFromCache(shortCode);
        if (cached != null) return cached;

        // Fallback to DB
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if (url.isExpired()) {
            throw new RuntimeException("URL has expired");
        }

        return url.getOriginalUrl();
    }

}
