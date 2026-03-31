package com.example.urlshortener.service;

import com.example.urlshortener.dto.TopUrlResponse;
import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public UrlResponse shortenUrl(UrlRequest request, Long userId){
        if(userId == null){
            throw new RuntimeException("Unauthorized");
        }

        // Step 1: check if already exists
        Url existing = urlRepository.findByOriginalUrl(request.getUrl()).orElse(null);

        if(existing != null){
            return UrlResponse.builder()
                    .shortUrl( baseUrl + "/" + existing.getShortCode())
                    .build();
        }

        // Step 2: determine shortCode (custom or random)
        String shortCode;
        if(request.getCustomCode() != null && !request.getCustomCode().isBlank()){
            boolean exists = urlRepository.findByShortCode(request.getCustomCode()).isPresent();
            if(exists){
                throw  new RuntimeException("Custom code already in use");
            }
            shortCode = request.getCustomCode();
        } else {
            shortCode = UUID.randomUUID().toString().substring(0, 6);
        }


        Url url = Url.builder()
                .originalUrl(request.getUrl())
                .shortCode(shortCode)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .hitCount(0L)
                .build();

        urlRepository.save(url);

        return UrlResponse.builder()
                .shortUrl(baseUrl + "/" + shortCode)
                .build();
    }

    @Override
    public String getOriginalUrl(String shortCode) {

        String cacheKey = "url:" + shortCode;

        // 1. Try Redis (fast path)
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        if (cachedUrl != null) {
            String hitKey = "hit:" + shortCode;
            redisTemplate.opsForValue().increment(hitKey);
            return cachedUrl;
        }

        // 2. Fallback to DB
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        // 3. Check expiry
        if (url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Link expired");
        }

        // 4. Cache it with TTL
        if (url.getExpiryDate() != null) {
            Duration ttl = Duration.between(LocalDateTime.now(), url.getExpiryDate());

            if (!ttl.isNegative()) {
                redisTemplate.opsForValue()
                        .set(cacheKey, url.getOriginalUrl(), ttl);
            }
        }

        // 5. Update hit count
        // url.setHitCount(url.getHitCount() + 1);
        // urlRepository.save(url);
        String hitKey = "hit:" + shortCode;
        redisTemplate.opsForValue().increment(hitKey);

        return url.getOriginalUrl();
    }

    @Override
    public void validateRateLimit(String clientIp){
        String key = "rate_limit:" + clientIp;
        Long count = redisTemplate.opsForValue().increment(key);

        // if first request, set ttl
        if(count != null && count == 1){
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        // Limit: 100 requests per minute
        if(count != null && count > 100){
            throw new RuntimeException("Too many requests");
        }
    }

    @Override
    public UrlStatsResponse getUrlStats(String shortCode, Long userId){

        if(userId == null){
            throw new RuntimeException("Unauthorized");
        }

        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        if(!url.getUserId().equals(userId)){
            throw new RuntimeException("Forbidden");
        }

        boolean isExpired = url.getExpiryDate() != null &&
                url.getExpiryDate().isBefore(LocalDateTime.now());

        return UrlStatsResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .hitCount(url.getHitCount())
                .createdAt(url.getCreatedAt())
                .expiryDate(url.getExpiryDate())
                .expired(isExpired)
                .build();
    }

    @Override
    public List<TopUrlResponse> getTopUrls(Long userId){
        if(userId == null){
            throw new RuntimeException("Unauthorized");
        }
        List<Url> urls = urlRepository.findTop5ByUserIdOrderByHitCountDesc(userId);

        return urls.stream()
                .map(url -> TopUrlResponse.builder()
                        .shortCode(url.getShortCode())
                        .originalUrl(url.getOriginalUrl())
                        .hitCount(url.getHitCount())
                        .build())
                .toList();
    }
}
