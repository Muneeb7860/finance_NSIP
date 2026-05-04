package com.example.education_service.controller;

import com.example.education_service.model.UserCourseProgress;
import com.example.education_service.repository.UserCourseProgressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
public class LearningGamificationController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private UserCourseProgressRepository progressRepository;

    /**
     * Complete a video course with gamified quiz.
     *
     * FLAW #13 FIX: Checks if the user has already completed this course.
     * If so, rejects the request to prevent infinite point farming.
     * Uses a DB unique constraint on (userId, courseId) as a safety net.
     */
    @PostMapping("/videos/{videoId}/complete")
    @Transactional
    public ResponseEntity<?> completeVideoWithInteractiveGamification(
            @PathVariable UUID videoId,
            @RequestParam UUID userId,
            @RequestParam UUID courseId,
            @RequestParam int interactiveQuizScore) {

        // FLAW #13 FIX: Prevent duplicate course completions
        if (progressRepository.existsByUserIdAndCourseIdAndCompletedTrue(userId, courseId)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Course already completed. Points were previously awarded.",
                    "courseId", courseId
            ));
        }

        // Gamification Rule: User must score > 70% on the interactive video quiz
        if (interactiveQuizScore < 70) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Quiz failed. Score " + interactiveQuizScore + "% is below the 70% threshold. Rewatch and try again."
            ));
        }

        // Gamification Logic: Award 50 base points + bonus for high scores
        int awardedPoints = 50 + (interactiveQuizScore - 70);

        // Persist completion record
        UserCourseProgress progress = progressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> {
                    UserCourseProgress p = new UserCourseProgress();
                    p.setUserId(userId);
                    p.setCourseId(courseId);
                    return p;
                });
        progress.setCompleted(true);
        progress.setQuizScore(interactiveQuizScore);
        progress.setCertified(interactiveQuizScore >= 90); // Certify for scores >= 90%
        progress.setCompletedAt(LocalDateTime.now());
        progressRepository.save(progress);

        // Publish event to Kafka for the rewards-service
        String eventPayload = String.format(
                "{\"userId\":\"%s\", \"courseId\":\"%s\", \"videoId\":\"%s\", \"pointsEarned\":%d, \"certified\":%b, \"event\":\"COURSE_COMPLETED\"}",
                userId, courseId, videoId, awardedPoints, progress.isCertified());
        kafkaTemplate.send("gamification.events", eventPayload);

        return ResponseEntity.ok(Map.of(
                "message", "Congratulations! You scored " + interactiveQuizScore + "% and earned " + awardedPoints + " points!",
                "points", awardedPoints,
                "certified", progress.isCertified(),
                "courseId", courseId
        ));
    }

    /**
     * Get user's learning progress across all courses.
     */
    @GetMapping("/progress/{userId}")
    public ResponseEntity<?> getUserProgress(@PathVariable UUID userId) {
        return ResponseEntity.ok(progressRepository.findByUserId(userId));
    }
}
