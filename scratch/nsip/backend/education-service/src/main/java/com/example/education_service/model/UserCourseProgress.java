package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks which courses a user has completed.
 * Prevents duplicate point awards from re-completing the same course.
 */
@Entity
@Table(name = "user_course_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "courseId"})
})
@Data
public class UserCourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID courseId;

    private boolean completed = false;
    private int quizScore = 0;
    private int attemptCount = 0;
    private boolean certified = false;
    private LocalDateTime completedAt;
}
