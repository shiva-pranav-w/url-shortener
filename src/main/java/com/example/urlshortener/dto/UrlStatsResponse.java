package com.example.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data@Builder
public class UrlStatsResponse {
    private String originalUrl;
    private String shortCode;
    private Long hitCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;
    private boolean expired;
}
