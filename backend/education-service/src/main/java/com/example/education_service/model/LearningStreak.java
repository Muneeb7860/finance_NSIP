package com.example.education_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Tracks daily learning streaks per user.
 * A streak breaks if the user misses a calendar day without completing any learning activity.
 */
@Entity
@Table(name = "learning_streaks")
public class LearningStreak {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private int currentStreak = 0;

    @Column(nullable = false)
    private int longestStreak = 0;

    @Column(nullable = false)
    private LocalDate lastActivityDate;

    public LearningStreak() {}

    public LearningStreak(UUID userId) {
        this.userId = userId;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.lastActivityDate = LocalDate.now().minusDays(1);
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    public LocalDate getLastActivityDate() { return lastActivityDate; }
    public void setLastActivityDate(LocalDate lastActivityDate) { this.lastActivityDate = lastActivityDate; }

    /** Bonus points awarded per streak milestone (7-day, 30-day, etc.) */
    public int getStreakBonusPoints() {
        if (currentStreak >= 30) return 500;
        if (currentStreak >= 14) return 200;
        if (currentStreak >= 7) return 100;
        return 0;
    }
}
