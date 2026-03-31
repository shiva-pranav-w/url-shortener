package com.example.urlshortener.service;

import com.example.urlshortener.dto.AuthRequest;

public interface AuthService {
    void register(AuthRequest request);
    String login(AuthRequest request);
}
