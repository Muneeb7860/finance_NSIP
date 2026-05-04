package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Wellness content: fitness tips and chronic disease home assistance programs.
 * Content is managed by ADMIN and displayed to CUSTOMER users.
 */
@Entity
@Table(name = "wellness_content")
@Data
public class WellnessContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentCategory category;

    @Enumerated(EnumType.STRING)
    private ContentType type;

    private String iconEmoji;
    private String tag;       // "Cardio", "Nutrition", etc.
    private boolean active = true;

    public enum ContentCategory {
        FITNESS_TIP,
        CHRONIC_CARE_PROGRAM
    }

    public enum ContentType {
        DIABETES, CARDIAC, RESPIRATORY, CHRONIC_PAIN,
        CARDIO, FLEXIBILITY, NUTRITION, RECOVERY, MENTAL_HEALTH
    }
}
