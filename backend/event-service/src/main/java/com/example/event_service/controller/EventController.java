package com.example.event_service.controller;

import com.example.event_service.model.Event;
import com.example.event_service.model.EventApproval;
import com.example.event_service.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "Business event proposals and 3-layer back-office approval")
public class EventController {

    @Autowired
    private EventService eventService;

    // =========================================================================
    // EMPLOYER Endpoints (Business owners propose events)
    // =========================================================================

    @Operation(summary = "Submit event proposal", description = "Business owner creates a new event. Enters DRAFT status and awaits L1 review.")
    @PostMapping("/propose")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> proposeEvent(@RequestBody Map<String, String> body) {
        try {
            String title = Objects.requireNonNull(body.get("title"), "Title is required");
            UUID createdByUserId = UUID.fromString(Objects.requireNonNull(body.get("createdByUserId"), "Creator User ID is required"));
            String organizationName = Objects.requireNonNull(body.get("organizationName"), "Organization name is required");

            Event event = new Event();
            event.setTitle(title);
            event.setDescription(body.get("description"));
            event.setType(Event.EventType.valueOf(body.getOrDefault("type", "PHYSICAL")));
            event.setCategory(Event.EventCategory.valueOf(body.getOrDefault("category", "OTHER")));
            event.setAttendancePointsReward(Integer.parseInt(body.getOrDefault("pointsReward", "50")));
            event.setMaxCapacity(body.containsKey("maxCapacity") ? Integer.parseInt(Objects.requireNonNull(body.get("maxCapacity"))) : null);
            event.setLocation(body.get("location"));

            if (body.containsKey("startTime")) {
                event.setStartTime(LocalDateTime.parse(Objects.requireNonNull(body.get("startTime"))));
            }
            if (body.containsKey("endTime")) {
                event.setEndTime(LocalDateTime.parse(Objects.requireNonNull(body.get("endTime"))));
            }

            Event saved = eventService.submitEventProposal(
                    event,
                    createdByUserId,
                    organizationName
            );
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "View my event proposals", description = "Business owner sees all events they've submitted and their current approval status.")
    @GetMapping("/my-events/{userId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<?> getMyEvents(@PathVariable UUID userId) {
        return ResponseEntity.ok(eventService.getMyEvents(userId));
    }

    // =========================================================================
    // BACK OFFICE Endpoints (3-layer approval)
    // =========================================================================

    @Operation(summary = "Get events pending at a specific approval level",
               description = "L1_REVIEWER sees DRAFT events, L2_MANAGER sees L1_APPROVED events, L3_DIRECTOR sees L2_APPROVED events.")
    @GetMapping("/pending/{level}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getPendingEvents(@PathVariable String level) {
        try {
            EventApproval.ApprovalLevel approvalLevel = EventApproval.ApprovalLevel.valueOf(level);
            return ResponseEntity.ok(eventService.getEventsPendingAtLevel(approvalLevel));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid level. Use: L1_REVIEWER, L2_MANAGER, or L3_DIRECTOR"));
        }
    }

    @Operation(summary = "Approve an event at a specific level",
               description = "Advances the event to the next approval stage. L3 approval makes the event LIVE.")
    @PatchMapping("/{eventId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveEvent(
            @PathVariable UUID eventId,
            @RequestBody Map<String, String> body) {
        try {
            UUID approverUserId = UUID.fromString(Objects.requireNonNull(body.get("approverUserId"), "Approver User ID is required"));
            String approverName = Objects.requireNonNull(body.get("approverName"), "Approver Name is required");
            EventApproval.ApprovalLevel level = EventApproval.ApprovalLevel.valueOf(Objects.requireNonNull(body.get("level"), "Approval level is required"));
            String comment = body.get("comment");

            Event result = eventService.approveEvent(
                    eventId,
                    approverUserId,
                    approverName,
                    level,
                    comment
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Reject an event at any level",
               description = "Rejects the event and notifies the business owner with the rejection reason.")
    @PatchMapping("/{eventId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectEvent(
            @PathVariable UUID eventId,
            @RequestBody Map<String, String> body) {
        try {
            UUID approverUserId = UUID.fromString(Objects.requireNonNull(body.get("approverUserId"), "Approver User ID is required"));
            String approverName = Objects.requireNonNull(body.get("approverName"), "Approver Name is required");
            EventApproval.ApprovalLevel level = EventApproval.ApprovalLevel.valueOf(Objects.requireNonNull(body.get("level"), "Approval level is required"));
            String rejectionReason = Objects.requireNonNull(body.get("rejectionReason"), "Rejection reason is required");

            Event result = eventService.rejectEvent(
                    eventId,
                    approverUserId,
                    approverName,
                    level,
                    rejectionReason
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "View full approval history", description = "Shows every approval/rejection action taken on this event, with timestamps and comments.")
    @GetMapping("/{eventId}/approval-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public ResponseEntity<?> getApprovalHistory(@PathVariable UUID eventId) {
        return ResponseEntity.ok(eventService.getApprovalHistory(eventId));
    }

    // =========================================================================
    // CUSTOMER Endpoints (Browse & RSVP to LIVE events only)
    // =========================================================================

    @Operation(summary = "Browse live events", description = "Returns only events that passed all 3 approval layers.")
    @GetMapping
    public ResponseEntity<?> getLiveEvents() {
        return ResponseEntity.ok(eventService.getLiveEvents());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getEventsByCategory(@PathVariable String category) {
        try {
            Event.EventCategory cat = Event.EventCategory.valueOf(category);
            return ResponseEntity.ok(eventService.getLiveEvents().stream()
                    .filter(e -> e.getCategory() == cat).toList());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid category"));
        }
    }

    @Operation(summary = "RSVP to a live event")
    @PostMapping("/{eventId}/rsvp")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> rsvp(@PathVariable UUID eventId, @RequestParam UUID userId) {
        try {
            return ResponseEntity.ok(eventService.rsvpToEvent(eventId, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Mark attendance at an event")
    @PostMapping("/attendance/{attendeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYER')")
    public ResponseEntity<?> markAttendance(@PathVariable UUID attendeeId) {
        return ResponseEntity.ok(eventService.markAttendance(attendeeId));
    }
}
