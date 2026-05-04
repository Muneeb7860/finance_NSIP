package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Immutable log of every qualifying platform activity.
 * Used for streak calculation and engagement analytics.
 */
@Entity
@Table(name = "activity_log")
@Data
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType activityType;

    private String referenceId; // courseId, sessionId, eventId, etc.
    private String details;     // "Completed BMC with SAR 12,000 monthly income"
    private int pointsEarned;
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * All qualifying activities that count toward streaks.
     */
    public enum ActivityType {
        BUDGET_CALCULATOR,       // BMC — ran the budget management calculator
        EMERGENCY_FUND_CALC,     // EMF — used the emergency fund calculator
        LESSON_COMPLETED,        // Watched a video / completed a lesson
        QUIZ_COMPLETED,          // Submitted a quiz
        COURSE_COMPLETED,        // Finished all modules in a course
        SESSION_ATTENDED,        // Attended an advisor session
        EVENT_ATTENDED,          // Attended a platform event
        MINI_GAME_PLAYED,        // Played a financial mini-game
        FINANCIAL_PLAN_CREATED,  // Used the financial planner
        LOAN_CALCULATOR_USED,    // Used the personal loan calculator
        PENSION_ESTIMATOR_USED   // Used the pension estimator
    }
}
