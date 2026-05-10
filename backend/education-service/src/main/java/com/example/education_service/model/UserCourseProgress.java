package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_course_progress")
@Data
public class UserCourseProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID courseId;

    private int progressPercentage;
    private int quizScore;
    private int attemptCount = 0;
    private boolean completed = false;
    private boolean certified = false;
    
    private LocalDateTime startedAt = LocalDateTime.now();
    private LocalDateTime completedAt;

    // Helper methods for boolean Lombok getters if needed by some IDEs
    public boolean isCompleted() { return completed; }
    public boolean isCertified() { return certified; }
}
