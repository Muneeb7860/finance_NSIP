package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks which chronic disease programs a user has enrolled in.
 * Enables home assistance scheduling and teleconsult tracking.
 */
@Entity
@Table(name = "chronic_care_enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "programId"})
})
@Data
public class ChronicCareEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID programId;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    private LocalDateTime enrolledAt = LocalDateTime.now();
    private LocalDateTime nextHomeVisit;
    private int teleconsultsCompleted = 0;

    public enum EnrollmentStatus {
        ACTIVE, PAUSED, COMPLETED, CANCELLED
    }
}
