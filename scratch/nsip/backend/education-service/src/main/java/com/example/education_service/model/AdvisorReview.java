package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Post-session review submitted by the customer.
 */
@Entity
@Table(name = "advisor_reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"sessionId"}) // One review per session
})
@Data
public class AdvisorReview {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private UUID advisorId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private int rating; // 1-5

    @Column(length = 1000)
    private String comment;

    private LocalDateTime createdAt = LocalDateTime.now();
}
