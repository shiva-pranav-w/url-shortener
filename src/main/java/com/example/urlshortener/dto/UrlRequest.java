package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UrlRequest {
    @NotBlank(message = "URL cannot be empty")
    @Pattern(
            regexp = "^(http|https)://.*$",
            message = "URL must start with http:// or https://"
    )
    private String url;

    @Pattern(
            regexp = "^[a-zA-Z0-9_-]{3,20}$",
            message = "Custom code must be 3-20 chars, alphanumeric, hyphen or underscore"
    )
    private String customCode;
}
