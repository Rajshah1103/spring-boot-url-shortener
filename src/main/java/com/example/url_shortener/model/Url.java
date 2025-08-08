package com.example.url_shortener.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String shortCode;


    @Column(nullable = false)
    private String originalUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private Long ttlInSeconds;

    public boolean isExpired() {
        if (ttlInSeconds == null) return false;
        return createdAt.plusSeconds(ttlInSeconds).isBefore(LocalDateTime.now());
    }
}
