package com.example.education_service.controller;

import com.example.education_service.service.AdvisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learning")
@Tag(name = "Financial Advisors", description = "Advisor self-service, booking, cancellation, and reviews")
public class AdvisorController {

    @Autowired private AdvisorService advisorService;

    // --- ADVISOR SELF-SERVICE ---

    @Operation(summary = "Register as a financial advisor")
    @PostMapping("/advisors/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            UUID userId = UUID.fromString(Objects.requireNonNull(body.get("userId"), "User ID is required"));
            String name = Objects.requireNonNull(body.get("name"), "Name is required");
            String specialty = Objects.requireNonNull(body.get("specialty"), "Specialty is required");
            String bio = Objects.requireNonNull(body.get("bio"), "Bio is required");
            
            return ResponseEntity.ok(advisorService.registerAdvisor(userId, name, specialty, bio));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Advisor views their schedule")
    @GetMapping("/advisors/{advisorId}/schedule")
    public ResponseEntity<?> getSchedule(@PathVariable UUID advisorId) {
        return ResponseEntity.ok(advisorService.getMySchedule(advisorId));
    }

    @Operation(summary = "Advisor approves a session request")
    @PostMapping("/sessions/{sessionId}/approve")
    public ResponseEntity<?> approveSession(@PathVariable UUID sessionId) {
        try {
            return ResponseEntity.ok(advisorService.approveSession(sessionId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Advisor rejects a session request")
    @PostMapping("/sessions/{sessionId}/reject")
    public ResponseEntity<?> rejectSession(@PathVariable UUID sessionId, @RequestBody Map<String, String> body) {
        try {
            String reason = body.getOrDefault("reason", "No reason provided");
            return ResponseEntity.ok(advisorService.rejectSession(sessionId, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Advisor views their ratings and reviews")
    @GetMapping("/advisors/{advisorId}/ratings")
    public ResponseEntity<?> getRatings(@PathVariable UUID advisorId) {
        return ResponseEntity.ok(advisorService.getMyRatings(advisorId));
    }

    @Operation(summary = "Advisor cancels a session (points restored to customer)")
    @PatchMapping("/sessions/{sessionId}/advisor-cancel")
    public ResponseEntity<?> advisorCancel(@PathVariable UUID sessionId, @RequestBody Map<String, String> body) {
        try {
            String reason = Objects.requireNonNull(body.get("reason"), "Reason is required");
            return ResponseEntity.ok(advisorService.advisorCancelSession(sessionId, reason));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Reschedule a session")
    @PatchMapping("/sessions/{sessionId}/reschedule")
    public ResponseEntity<?> reschedule(@PathVariable UUID sessionId, @RequestBody Map<String, String> body) {
        try {
            String newTimeStr = Objects.requireNonNull(body.get("newTime"), "New time is required");
            return ResponseEntity.ok(advisorService.rescheduleSession(sessionId, LocalDateTime.parse(newTimeStr)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // --- CUSTOMER ENDPOINTS ---

    @Operation(summary = "Browse all available advisors")
    @GetMapping("/advisors")
    public ResponseEntity<?> browseAdvisors() {
        return ResponseEntity.ok(advisorService.getActiveAdvisors());
    }

    @Operation(summary = "Book a session (starts as PENDING_APPROVAL)")
    @PostMapping("/sessions/book")
    public ResponseEntity<?> book(@RequestBody Map<String, String> body) {
        try {
            UUID customerId = UUID.fromString(Objects.requireNonNull(body.get("customerId"), "Customer ID is required"));
            UUID advisorId = UUID.fromString(Objects.requireNonNull(body.get("advisorId"), "Advisor ID is required"));
            String scheduledAtStr = Objects.requireNonNull(body.get("scheduledAt"), "Scheduled at is required");
            
            return ResponseEntity.ok(advisorService.bookSession(
                    customerId,
                    advisorId,
                    LocalDateTime.parse(scheduledAtStr)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Customer cancels a session (points restored)")
    @PatchMapping("/sessions/{sessionId}/customer-cancel")
    public ResponseEntity<?> customerCancel(@PathVariable UUID sessionId, @RequestBody Map<String, String> body) {
        try {
            String reason = Objects.requireNonNull(body.get("reason"), "Reason is required");
            return ResponseEntity.ok(advisorService.customerCancelSession(sessionId, reason));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Submit a review for a completed session")
    @PostMapping("/sessions/{sessionId}/review")
    public ResponseEntity<?> review(@PathVariable UUID sessionId, @RequestBody Map<String, String> body) {
        try {
            UUID customerId = UUID.fromString(Objects.requireNonNull(body.get("customerId"), "Customer ID is required"));
            int rating = Integer.parseInt(Objects.requireNonNull(body.get("rating"), "Rating is required"));
            String comment = Objects.requireNonNull(body.get("comment"), "Comment is required");
            
            return ResponseEntity.ok(advisorService.submitReview(
                    sessionId,
                    customerId,
                    rating,
                    comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get customer's session history")
    @GetMapping("/sessions/customer/{customerId}")
    public ResponseEntity<?> customerSessions(@PathVariable UUID customerId) {
        return ResponseEntity.ok(advisorService.getCustomerSessions(customerId));
    }
}
