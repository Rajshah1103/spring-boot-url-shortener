package com.example.url_shortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlRequest {

    @NotBlank
    @Size(max = 2048)
    private String originalUrl;

    @PositiveOrZero
    private Long ttlSeconds;
}
