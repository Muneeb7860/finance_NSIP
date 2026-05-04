package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Financial advisor who can list themselves on the platform.
 * Advisors manage their own availability and see their ratings.
 */
@Entity
@Table(name = "advisor_profiles")
@Data
public class AdvisorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId; // Links to auth-service user

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialty; // "Retirement Planning", "Tax Optimization", etc.

    @Column(length = 2000)
    private String bio;

    private int sessionDurationMinutes = 30;
    private int pointsCost = 1000;
    private boolean active = true;

    // Denormalized rating for fast reads (updated on each review)
    private double averageRating = 0.0;
    private int totalReviews = 0;
    private int totalSessions = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
}
