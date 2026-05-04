package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String thumbnailUrl;
    private String videoUrl;
    private int durationMinutes;
    private int pointsReward = 50;
    private int quizQuestionCount = 10;
    private int passingScore = 70;
    private int certificationScore = 90;
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    private CourseCategory category;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty;

    public enum CourseCategory {
        FINANCIAL_LITERACY, INVESTMENT, INSURANCE, TAX, RETIREMENT, BUDGETING
    }

    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
