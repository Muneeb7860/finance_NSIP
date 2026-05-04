package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Booking for a 1-on-1 financial advisor session.
 * Points are deducted on booking and restored on cancellation.
 */
@Entity
@Table(name = "advisor_sessions")
@Data
public class AdvisorSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID advisorId;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    private int pointsCharged;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.BOOKED;

    private LocalDateTime scheduledAt;
    private LocalDateTime bookedAt = LocalDateTime.now();
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
    private String cancellationReason;

    public enum SessionStatus {
        BOOKED,
        RESCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED_BY_CUSTOMER,
        CANCELLED_BY_ADVISOR,
        NO_SHOW
    }
}
