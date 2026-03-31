package com.example.urlshortener.service;

import com.example.urlshortener.dto.TopUrlResponse;
import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;

import java.util.List;

public interface UrlService {
    UrlResponse shortenUrl(UrlRequest request, Long userId);
    String getOriginalUrl(String shortCode);
    void validateRateLimit(String clientIp);
    UrlStatsResponse getUrlStats(String shortCode, Long userId);
    List<TopUrlResponse> getTopUrls(Long userId);
}
