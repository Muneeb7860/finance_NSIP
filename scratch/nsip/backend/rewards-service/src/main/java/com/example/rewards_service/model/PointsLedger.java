package com.example.rewards_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "points_ledger")
@Data
public class PointsLedger {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    private int pointDelta; // positive = earned, negative = spent
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
}
