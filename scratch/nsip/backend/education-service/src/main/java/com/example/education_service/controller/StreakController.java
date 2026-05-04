package com.example.education_service.controller;

import com.example.education_service.model.ActivityLog;
import com.example.education_service.service.StreakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/streaks")
@Tag(name = "Activity Streaks", description = "Weekly (4 days) and monthly (4 weeks) streak tracking across all platform features")
public class StreakController {

    @Autowired private StreakService streakService;

    @Operation(summary = "Record a platform activity",
               description = "Call this from any feature: BMC, EMF, quiz, lesson, session, event, mini-game, etc. Automatically updates the streak.")
    @PostMapping("/record")
    public ResponseEntity<?> recordActivity(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(streakService.recordActivity(
                    UUID.fromString(body.get("userId")),
                    ActivityLog.ActivityType.valueOf(body.get("activityType")),
                    body.getOrDefault("referenceId", ""),
                    body.getOrDefault("details", ""),
                    Integer.parseInt(body.getOrDefault("pointsEarned", "0"))
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get user's streak dashboard",
               description = "Shows active days this week, weekly/monthly streaks, and recent activity log")
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<?> getStreakDashboard(@PathVariable UUID userId) {
        return ResponseEntity.ok(streakService.getStreakDashboard(userId));
    }
}
