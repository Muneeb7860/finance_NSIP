package com.example.saga_orchestrator.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_state")
@Data
public class SagaState {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sagaId;

    @Column(nullable = false)
    private UUID claimId;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStep currentStep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    private String failureReason;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum SagaStep {
        INITIATED,
        FUNDS_LOCK_REQUESTED,
        FUNDS_LOCKED,
        PAYMENT_REQUESTED,
        PAYMENT_COMPLETED,
        COMPENSATING_UNLOCK
    }

    public enum SagaStatus {
        RUNNING, COMPLETED, FAILED, COMPENSATED
    }
}
