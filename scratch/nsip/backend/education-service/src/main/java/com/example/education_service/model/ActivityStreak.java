package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Tracks platform-wide activity streaks per user.
 *
 * An "activity" is ANY meaningful platform interaction:
 *   - BMC (Budget Management Calculator)
 *   - EMF (Emergency Fund Calculator)
 *   - Completing a lesson, quiz, or course
 *   - Attending an advisor session
 *   - Attending an event
 *   - Playing mini-games
 *   - Using the financial planner
 *
 * Streak Rules:
 *   - WEEKLY STREAK:  User is active on ≥4 distinct days in a calendar week
 *   - MONTHLY STREAK: User achieves 4 consecutive weekly streaks
 *
 * Bonus Points:
 *   - Weekly streak achieved:  +100 pts
 *   - Monthly streak achieved: +500 pts
 */
@Entity
@Table(name = "activity_streaks")
@Data
public class ActivityStreak {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    // --- Daily tracking ---

    /** Distinct days the user has been active in the current calendar week (Sun–Sat). */
    @Column(nullable = false)
    private int activeDaysThisWeek = 0;

    /** ISO week number of the last tracked week (to detect week rollover). */
    @Column(nullable = false)
    private int lastTrackedWeekNumber = 0;

    @Column(nullable = false)
    private int lastTrackedYear = 0;

    @Column(nullable = false)
    private LocalDate lastActivityDate;

    // --- Weekly streak ---

    /** Consecutive weeks the user hit ≥4 active days. */
    @Column(nullable = false)
    private int currentWeeklyStreak = 0;

    @Column(nullable = false)
    private int longestWeeklyStreak = 0;

    // --- Monthly streak ---

    /** Number of monthly streaks completed (4 consecutive weekly streaks). */
    @Column(nullable = false)
    private int monthlyStreaksCompleted = 0;

    // --- Activity log for current week (stored as comma-separated day numbers) ---

    /** Days of the week the user was active, e.g. "1,3,4,6" */
    @Column(length = 50)
    private String activeDaysLog = "";

    // --- Helpers ---

    /** Check if a specific day-of-week is already recorded this week. */
    public boolean isDayRecorded(int dayOfWeek) {
        if (activeDaysLog == null || activeDaysLog.isEmpty()) return false;
        for (String d : activeDaysLog.split(",")) {
            if (!d.isEmpty() && Integer.parseInt(d.trim()) == dayOfWeek) return true;
        }
        return false;
    }

    /** Record a day-of-week as active. */
    public void recordDay(int dayOfWeek) {
        if (isDayRecorded(dayOfWeek)) return;
        activeDaysLog = activeDaysLog.isEmpty() ? String.valueOf(dayOfWeek) : activeDaysLog + "," + dayOfWeek;
        activeDaysThisWeek++;
    }

    /** Reset for a new week. */
    public void startNewWeek(int weekNumber, int year) {
        activeDaysThisWeek = 0;
        activeDaysLog = "";
        lastTrackedWeekNumber = weekNumber;
        lastTrackedYear = year;
    }

    /** Is the weekly streak threshold met? (≥4 active days) */
    public boolean isWeeklyStreakMet() {
        return activeDaysThisWeek >= 4;
    }

    /** Bonus points for streak milestones. */
    public int getStreakBonusPoints() {
        if (currentWeeklyStreak > 0 && currentWeeklyStreak % 4 == 0) return 500; // Monthly
        if (isWeeklyStreakMet()) return 100; // Weekly
        return 0;
    }
}
