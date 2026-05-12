package com.example.rewards_service.controller;

import com.example.rewards_service.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rewards")
public class RewardsController {

    @Autowired
    private RewardsService rewardsService;

    @GetMapping("/balance/{userId}")
    public ResponseEntity<?> getBalance(@PathVariable UUID userId) {
        return ResponseEntity.ok(Map.of("userId", userId, "balance", rewardsService.getBalance(userId)));
    }

    @PostMapping("/award")
    public ResponseEntity<?> awardPoints(@RequestBody Map<String, String> body) {
        UUID userId = UUID.fromString(Objects.requireNonNull(body.get("userId"), "User ID is required"));
        int points = Integer.parseInt(Objects.requireNonNull(body.get("points"), "Points are required"));
        String description = Objects.requireNonNull(body.get("description"), "Description is required");
        
        return ResponseEntity.ok(rewardsService.awardPoints(userId, points, description));
    }

    @PostMapping("/sessions/book")
    public ResponseEntity<?> bookSession(@RequestBody Map<String, String> body) {
        try {
            UUID userId = UUID.fromString(Objects.requireNonNull(body.get("userId"), "User ID is required"));
            UUID advisorId = UUID.fromString(Objects.requireNonNull(body.get("advisorId"), "Advisor ID is required"));
            String scheduledTimeStr = Objects.requireNonNull(body.get("scheduledTime"), "Scheduled time is required");
            
            return ResponseEntity.ok(rewardsService.bookSession(
                    userId,
                    advisorId,
                    LocalDateTime.parse(scheduledTimeStr)
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/sessions/{sessionId}/cancel")
    public ResponseEntity<?> cancelSession(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(rewardsService.cancelSession(sessionId));
    }

    @PatchMapping("/sessions/{sessionId}/reschedule")
    public ResponseEntity<?> rescheduleSession(@PathVariable UUID sessionId, @RequestParam String newTime) {
        return ResponseEntity.ok(rewardsService.rescheduleSession(sessionId, LocalDateTime.parse(newTime)));
    }

    @PostMapping("/redeem")
    public ResponseEntity<?> redeem(@RequestBody Map<String, String> body) {
        try {
            UUID userId = UUID.fromString(Objects.requireNonNull(body.get("userId"), "User ID is required"));
            String itemName = Objects.requireNonNull(body.get("itemName"), "Item name is required");
            int cost = Integer.parseInt(Objects.requireNonNull(body.get("cost"), "Cost is required"));
            
            String code = rewardsService.redeemPoints(userId, itemName, cost);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "voucherCode", code));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
