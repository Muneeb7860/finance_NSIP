package com.example.claim_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
@Data
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimType claimType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum ClaimType {
        MEDICAL, JOB_LOSS, RETIREMENT, ACCIDENT, END_OF_LIFE, PERSONAL_LOAN, EMERGENCY_RELIEF
    }

    public enum ClaimStatus {
        PENDING, UNDER_REVIEW, APPROVED, REJECTED, DISBURSED, COMPLETED, FAILED
    }
}
