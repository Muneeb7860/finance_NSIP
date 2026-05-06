package com.example.education_service.controller;

import com.example.education_service.model.*;
import com.example.education_service.service.LearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
@Tag(name = "Learning & Gamification", description = "LMS courses, quizzes, streaks, certificates, and gamification")
public class LearningController {

    @Autowired private LearningService learningService;

    @Operation(summary = "Get all active courses")
    @GetMapping("/list")
    public ResponseEntity<?> getCourses() { return ResponseEntity.ok(learningService.getAllCourses()); }

    @Operation(summary = "Get courses by category")
    @GetMapping("/list/category/{category}")
    public ResponseEntity<?> getCoursesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(learningService.getCoursesByCategory(Course.CourseCategory.valueOf(category)));
    }

    @Operation(summary = "Submit quiz answers", description = "Scores the quiz, awards points, updates streak, and issues certificate if score >= 90%")
    @PostMapping("/quiz/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody Map<String, String> body) {
        try {
            UUID userId = UUID.fromString(Objects.requireNonNull(body.get("userId"), "User ID is required"));
            UUID courseId = UUID.fromString(Objects.requireNonNull(body.get("courseId"), "Course ID is required"));
            int score = Integer.parseInt(Objects.requireNonNull(body.get("score"), "Score is required"));
            String userName = body.getOrDefault("userName", "Contributor");
            
            return ResponseEntity.ok(learningService.submitQuiz(
                    Objects.requireNonNull(userId),
                    Objects.requireNonNull(courseId),
                    score,
                    userName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get user's learning dashboard", description = "Returns courses completed, streak, certificates, and progress")
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getDashboard(@PathVariable UUID userId) {
        return ResponseEntity.ok(learningService.getLearningDashboard(userId));
    }

    @Operation(summary = "Get user's current streak")
    @GetMapping("/streak/{userId}")
    public ResponseEntity<?> getStreak(@PathVariable UUID userId) {
        return ResponseEntity.ok(learningService.getStreak(userId));
    }

    @Operation(summary = "Get user's certificates")
    @GetMapping("/certificates/{userId}")
    public ResponseEntity<?> getCertificates(@PathVariable UUID userId) {
        return ResponseEntity.ok(learningService.getUserCertificates(userId));
    }
}
