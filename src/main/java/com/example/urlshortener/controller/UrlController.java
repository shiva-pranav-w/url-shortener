package com.example.urlshortener.controller;

import com.example.urlshortener.dto.TopUrlResponse;
import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.dto.UrlStatsResponse;
import com.example.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/url")
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;

    @PostMapping("/shorten")
    public UrlResponse shorten(@Valid @RequestBody UrlRequest request,
                               HttpServletRequest httpRequest){

        Long userId = (Long) httpRequest.getAttribute("userId");
        return urlService.shortenUrl(request, userId);
    }

    @GetMapping("/{shortCode}/stats")
    public UrlStatsResponse getStats(@PathVariable String shortCode, HttpServletRequest request){

        Long userId = (Long) request.getAttribute("userId");
        return urlService.getUrlStats(shortCode, userId);
    }

    @GetMapping("/top")
    public List<TopUrlResponse> getTopUrls(HttpServletRequest request){
        Long userId = (Long) request.getAttribute("userId");
        return urlService.getTopUrls(userId);
    }
}
