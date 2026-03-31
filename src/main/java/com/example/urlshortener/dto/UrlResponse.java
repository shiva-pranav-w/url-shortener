package com.example.urlshortener.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UrlResponse {
    private String shortUrl;
}
