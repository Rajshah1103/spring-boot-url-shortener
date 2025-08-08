package com.example.url_shortener.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlResponse {

    private String shortUrl;
    private String originalUrl;
}
