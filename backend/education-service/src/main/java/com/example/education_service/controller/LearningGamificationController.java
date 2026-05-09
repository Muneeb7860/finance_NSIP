package com.example.education_service.controller;

import com.example.education_service.service.LearningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.lang.NonNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
@Tag(name = "Learning Gamification", description = "Endpoints for course progression and interactive gamification")
public class LearningGamificationController {

    private final LearningService learningService;

    public LearningGamificationController(LearningService learningService) {
        this.learningService = learningService;
    }

    /**
     * Complete a video course with gamified quiz.
     *
     * FLAW #13 FIX: Checks if the user has already completed this course.
     * If so, rejects the request to prevent infinite point farming.
     * Uses a DB unique constraint on (userId, courseId) as a safety net.
     */
    @PostMapping("/videos/{videoId}/complete")
    @PreAuthorize("#userId.toString().equals(authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Complete a video course", description = "Records completion, validates quiz score, and awards gamification points.")
    public ResponseEntity<Map<String, Object>> completeVideoWithInteractiveGamification(
            @PathVariable @NonNull UUID videoId,
            @RequestParam @NonNull UUID userId,
            @RequestParam @NonNull UUID courseId,
            @RequestParam int interactiveQuizScore) {

        try {
            Map<String, Object> result = learningService.completeVideoWithGamification(userId, videoId, courseId, interactiveQuizScore);
            if (result.containsKey("error")) {
                return ResponseEntity.badRequest().body(result);
            }
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get user's learning progress across all courses.
     */
    @GetMapping("/progress/{userId}")
    @PreAuthorize("#userId.toString().equals(authentication.name) or hasRole('ADMIN')")
    @Operation(summary = "Get user progress", description = "Retrieve a user's course completions and scores.")
    public ResponseEntity<?> getUserProgress(@PathVariable @NonNull UUID userId) {
        return ResponseEntity.ok(learningService.getUserProgress(userId));
    }
}
