package com.example.education_service.service;

import com.example.education_service.model.*;
import com.example.education_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class LearningService {

    @Autowired private CourseRepository courseRepo;
    @Autowired private UserCourseProgressRepository progressRepo;
    @Autowired private CertificateRepository certRepo;
    @Autowired private LearningStreakRepository streakRepo;
    @Autowired private StreakService streakService;
    @Autowired private KafkaTemplate<String, String> kafkaTemplate;

    private static final AtomicLong CERT_COUNTER = new AtomicLong(1);

    public LearningStreak getStreak(UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return streakRepo.findByUserId(userId).orElse(new LearningStreak(userId));
    }

    // =========================================================================
    // LMS: Courses
    // =========================================================================

    public List<Course> getAllCourses() { return courseRepo.findByActiveTrue(); }

    public List<Course> getCoursesByCategory(Course.CourseCategory cat) {
        return courseRepo.findByCategoryAndActiveTrue(cat);
    }

    /**
     * Submit a quiz with retry support.
     *
     * POINT ECONOMICS (guarantees every user can reach 1,000 pts):
     * - 6 courses × ~100-150 pts each = 600-900 pts on first attempts
     * - Streak bonuses: 7-day = 100, 14-day = 200, 30-day = 500
     * - Event attendance: 50 pts each
     * - Retries allowed but with diminishing returns:
     *   Attempt 1 = 100% points, Attempt 2 = 50%, Attempt 3+ = 25%
     */
    @Transactional
    public Map<String, Object> submitQuiz(@NonNull UUID userId, @NonNull UUID courseId, int score, String userName) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));

        boolean passed = score >= course.getPassingScore();
        boolean certified = score >= course.getCertificationScore();

        // Get or create progress — track attempt count
        UserCourseProgress progress = progressRepo.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> { UserCourseProgress p = new UserCourseProgress(); p.setUserId(userId); p.setCourseId(courseId); return p; });

        int attempt = progress.getAttemptCount() + 1;
        progress.setAttemptCount(attempt);

        // Already completed on a previous attempt — allow retry but fewer points
        boolean firstCompletion = !progress.isCompleted() && passed;

        // Diminishing returns: attempt 1 = 100%, attempt 2 = 50%, attempt 3+ = 25%
        double multiplier = attempt == 1 ? 1.0 : attempt == 2 ? 0.5 : 0.25;
        int basePoints = course.getPointsReward() + Math.max(0, score - course.getPassingScore());
        int pointsEarned = passed ? (int) Math.ceil(basePoints * multiplier) : 0;

        // Only award points if this is a new pass or an improved score
        if (passed && score <= progress.getQuizScore() && progress.isCompleted()) {
            pointsEarned = 0; // No points for equal/lower score on retry
        }

        progress.setCompleted(passed || progress.isCompleted()); // Once passed, stays passed
        if (score > progress.getQuizScore()) progress.setQuizScore(score); // Track best score
        progress.setCertified(certified || progress.isCertified());
        if (firstCompletion) progress.setCompletedAt(LocalDateTime.now());
        progressRepo.save(progress);

        // Record activity for the new weekly/monthly streak system
        Map<String, Object> streakInfo = streakService.recordActivity(
                userId, ActivityLog.ActivityType.QUIZ_COMPLETED,
                courseId.toString(), "Quiz score: " + score + "%", pointsEarned);

        // Issue certificate on first certification
        Certificate cert = null;
        if (certified && !certRepo.existsByUserIdAndCourseId(userId, courseId)) {
            cert = new Certificate();
            cert.setUserId(userId);
            cert.setCourseId(courseId);
            cert.setCourseName(course.getTitle());
            cert.setUserName(userName);
            cert.setCertificateNumber("NSIP-CERT-" + LocalDate.now().getYear() + "-" + String.format("%05d", CERT_COUNTER.getAndIncrement()));
            cert.setQuizScore(score);
            certRepo.save(cert);
        }

        // Award points via Kafka
        if (pointsEarned > 0) {
            kafkaTemplate.send("gamification.events", String.format(
                    "{\"userId\":\"%s\",\"courseId\":\"%s\",\"pointsEarned\":%d,\"event\":\"COURSE_COMPLETED\",\"certified\":%b,\"attempt\":%d}",
                    userId, courseId, pointsEarned, certified, attempt));
        }

        return Map.of(
                "passed", passed, "score", score, "pointsEarned", pointsEarned,
                "certified", certified,
                "activeDaysThisWeek", streakInfo.get("activeDaysThisWeek"),
                "weeklyStreakMet", streakInfo.get("weeklyStreakMet"),
                "attempt", attempt, "multiplier", multiplier,
                "certificate", cert != null ? cert.getCertificateNumber() : "N/A"
        );
    }
    // Streak management is now handled by StreakService (weekly/monthly model)

    // =========================================================================
    // Certificates
    // =========================================================================

    public List<Certificate> getUserCertificates(UUID userId) {
        return certRepo.findByUserId(userId);
    }

    // =========================================================================
    // Dashboard: Full user learning profile
    // =========================================================================

    public Map<String, Object> getLearningDashboard(UUID userId) {
        List<UserCourseProgress> progress = progressRepo.findByUserId(userId);
        LearningStreak streak = streakRepo.findByUserId(userId).orElse(null);
        List<Certificate> certs = certRepo.findByUserId(userId);
        long completed = progress.stream().filter(UserCourseProgress::isCompleted).count();
        long total = courseRepo.findByActiveTrue().size();

        return Map.of(
                "coursesCompleted", completed, "totalCourses", total,
                "certificates", certs.size(),
                "currentStreak", streak != null ? streak.getCurrentStreak() : 0,
                "longestStreak", streak != null ? streak.getLongestStreak() : 0,
                "progress", progress, "certificateList", certs
        );
    }
}
