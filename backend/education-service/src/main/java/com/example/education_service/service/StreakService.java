package com.example.education_service.service;

import com.example.education_service.model.ActivityLog;
import com.example.education_service.model.ActivityStreak;
import com.example.education_service.repository.ActivityLogRepository;
import com.example.education_service.repository.ActivityStreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Centralised streak engine.
 *
 * Called by every feature when a user performs a qualifying activity:
 *   BMC, EMF, quiz, lesson, session, event, mini-game, planner, etc.
 *
 * Streak rules:
 *   - WEEKLY STREAK:  ≥4 distinct active days in a calendar week → +100 pts
 *   - MONTHLY STREAK: 4 consecutive weekly streaks → +500 pts
 */
@Service
@Slf4j
public class StreakService {

    @Autowired private ActivityStreakRepository streakRepo;
    @Autowired private ActivityLogRepository logRepo;
    @Autowired private KafkaTemplate<String, String> kafkaTemplate;

    private static final WeekFields WEEK_FIELDS = WeekFields.of(Locale.getDefault());

    /**
     * Record a platform activity. Called from any service/controller.
     * Returns the updated streak info.
     */
    @Transactional
    public Map<String, Object> recordActivity(UUID userId, ActivityLog.ActivityType type, String referenceId, String details, int pointsEarned) {
        // 1. Log the activity
        ActivityLog entry = new ActivityLog();
        entry.setUserId(userId);
        entry.setActivityType(type);
        entry.setReferenceId(referenceId);
        entry.setDetails(details);
        entry.setPointsEarned(pointsEarned);
        logRepo.save(entry);

        // 2. Update streak
        LocalDate today = LocalDate.now();
        int currentWeek = today.get(WEEK_FIELDS.weekOfWeekBasedYear());
        int currentYear = today.getYear();
        int dayOfWeek = today.getDayOfWeek().getValue(); // 1=Mon ... 7=Sun

        ActivityStreak streak = streakRepo.findByUserId(userId).orElseGet(() -> {
            ActivityStreak s = new ActivityStreak();
            s.setUserId(userId);
            s.setLastActivityDate(today);
            s.setLastTrackedWeekNumber(currentWeek);
            s.setLastTrackedYear(currentYear);
            return s;
        });

        // Detect week rollover
        boolean newWeek = (currentWeek != streak.getLastTrackedWeekNumber()) || (currentYear != streak.getLastTrackedYear());

        if (newWeek) {
            // Evaluate the previous week before resetting
            boolean previousWeekHit = streak.isWeeklyStreakMet();

            if (previousWeekHit) {
                streak.setCurrentWeeklyStreak(streak.getCurrentWeeklyStreak() + 1);
                if (streak.getCurrentWeeklyStreak() > streak.getLongestWeeklyStreak()) {
                    streak.setLongestWeeklyStreak(streak.getCurrentWeeklyStreak());
                }

                // Check monthly milestone (every 4 weekly streaks)
                if (streak.getCurrentWeeklyStreak() % 4 == 0) {
                    streak.setMonthlyStreaksCompleted(streak.getMonthlyStreaksCompleted() + 1);
                    kafkaTemplate.send("gamification.events", String.format(
                            "{\"userId\":\"%s\",\"pointsEarned\":500,\"event\":\"MONTHLY_STREAK\"}", userId));
                    log.info("🏆 MONTHLY STREAK for user {}! {} months total", userId, streak.getMonthlyStreaksCompleted());
                } else {
                    kafkaTemplate.send("gamification.events", String.format(
                            "{\"userId\":\"%s\",\"pointsEarned\":100,\"event\":\"WEEKLY_STREAK\"}", userId));
                    log.info("🔥 WEEKLY STREAK for user {}! {} weeks in a row", userId, streak.getCurrentWeeklyStreak());
                }
            } else {
                // Weekly streak broken
                if (streak.getCurrentWeeklyStreak() > 0) {
                    log.info("Streak broken for user {}. Was {} weeks.", userId, streak.getCurrentWeeklyStreak());
                }
                streak.setCurrentWeeklyStreak(0);
            }

            streak.startNewWeek(currentWeek, currentYear);
        }

        // Record today's activity
        streak.recordDay(dayOfWeek);
        streak.setLastActivityDate(today);
        streakRepo.save(streak);

        log.info("Activity recorded: user={} type={} day={}/7 this week, {} weekly streak",
                userId, type, streak.getActiveDaysThisWeek(), streak.getCurrentWeeklyStreak());

        return Map.of(
                "activeDaysThisWeek", streak.getActiveDaysThisWeek(),
                "weeklyStreakMet", streak.isWeeklyStreakMet(),
                "currentWeeklyStreak", streak.getCurrentWeeklyStreak(),
                "monthlyStreaksCompleted", streak.getMonthlyStreaksCompleted(),
                "daysNeededForWeeklyStreak", Math.max(0, 4 - streak.getActiveDaysThisWeek()),
                "weeksNeededForMonthlyStreak", 4 - (streak.getCurrentWeeklyStreak() % 4),
                "activityType", type.name()
        );
    }

    /** Get user's streak dashboard. */
    public Map<String, Object> getStreakDashboard(UUID userId) {
        ActivityStreak streak = streakRepo.findByUserId(userId).orElse(null);
        List<ActivityLog> recentActivity = logRepo.findTop20ByUserIdOrderByTimestampDesc(userId);

        if (streak == null) {
            return Map.of("activeDaysThisWeek", 0, "currentWeeklyStreak", 0,
                    "monthlyStreaksCompleted", 0, "recentActivity", List.of());
        }

        return Map.of(
                "activeDaysThisWeek", streak.getActiveDaysThisWeek(),
                "activeDaysLog", streak.getActiveDaysLog(),
                "weeklyStreakMet", streak.isWeeklyStreakMet(),
                "currentWeeklyStreak", streak.getCurrentWeeklyStreak(),
                "longestWeeklyStreak", streak.getLongestWeeklyStreak(),
                "monthlyStreaksCompleted", streak.getMonthlyStreaksCompleted(),
                "daysNeededForWeeklyStreak", Math.max(0, 4 - streak.getActiveDaysThisWeek()),
                "recentActivity", recentActivity
        );
    }
}
