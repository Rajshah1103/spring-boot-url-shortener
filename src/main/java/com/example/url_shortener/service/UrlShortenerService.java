package com.example.url_shortener.service;

import com.example.url_shortener.dto.UrlRequest;
import com.example.url_shortener.dto.UrlResponse;

public interface UrlShortenerService {
    UrlResponse shortenUrl (UrlRequest request);
    String getOriginalUrl (String shortUrl);
}
