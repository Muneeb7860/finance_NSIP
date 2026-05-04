package com.example.contribution_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Tracks locked funds during a Saga transaction.
 * When the Saga orchestrator requests a fund lock, a record is created here.
 * If the Saga fails, the compensating transaction deletes or marks the lock as released.
 */
@Entity
@Table(name = "fund_locks")
@Data
public class FundLock {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID claimId;

    @Column(nullable = false)
    private BigDecimal lockedAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LockStatus status = LockStatus.LOCKED;

    public enum LockStatus {
        LOCKED, RELEASED, DISBURSED
    }
}
