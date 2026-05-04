package com.example.review_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String featureName; // e.g., "Loan Application", "LMS Course", "Advisor Session"

    @Column(nullable = false)
    private int rating; // 1-5 stars

    private String comment;
    private LocalDateTime createdAt = LocalDateTime.now();
}
