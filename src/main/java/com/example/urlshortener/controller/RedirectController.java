package com.example.urlshortener.controller;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import com.example.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {

        String clientIp = request.getRemoteAddr();
        urlService.validateRateLimit(clientIp);

        String originalUrl = urlService.getOriginalUrl(shortCode);
        response.sendRedirect(originalUrl);
    }
}