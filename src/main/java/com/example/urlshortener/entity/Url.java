package com.example.urlshortener.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // primary key
    private String originalUrl;
    private String shortCode;
    private Long userId;
    private LocalDateTime createdAt; // sorting, analytics

    @Column(nullable = false)
    private Long hitCount = 0L;

    private LocalDateTime expiryDate;
}
