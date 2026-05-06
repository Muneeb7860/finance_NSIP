package com.example.event_service.service;

import com.example.event_service.model.Event;
import com.example.event_service.model.EventApproval;
import com.example.event_service.model.EventAttendee;
import com.example.event_service.repository.EventApprovalRepository;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.EventAttendeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventAttendeeRepository attendeeRepository;

    @Autowired
    private EventApprovalRepository approvalRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    // =========================================================================
    // EMPLOYER: Create Event Proposal
    // =========================================================================

    /**
     * Business owner submits a new event proposal.
     * The event starts in DRAFT status and enters the 3-layer approval pipeline.
     */
    @Transactional
    public Event submitEventProposal(Event event, UUID createdByUserId, String organizationName) {
        event.setCreatedByUserId(createdByUserId);
        event.setOrganizationName(organizationName);
        event.setApprovalStatus(Event.ApprovalStatus.DRAFT);

        Event saved = eventRepository.save(event);
        log.info("Event proposal submitted: '{}' by org '{}' [{}] — awaiting L1 review",
                saved.getTitle(), organizationName, saved.getId());

        // Notify back office that a new event is pending review
        kafkaTemplate.send("notification.command.send",
                String.format("{\"channel\":\"INTERNAL\", \"message\":\"New event proposal '%s' from %s awaiting L1 review.\"}",
                        saved.getTitle(), organizationName));

        return saved;
    }

    /**
     * Business owner views their own submitted events and their approval status.
     */
    public List<Event> getMyEvents(UUID userId) {
        return eventRepository.findByCreatedByUserId(userId);
    }

    // =========================================================================
    // BACK OFFICE: 3-Layer Approval Pipeline
    // =========================================================================

    /**
     * Get events pending review at a specific approval level.
     */
    public List<Event> getEventsPendingAtLevel(EventApproval.ApprovalLevel level) {
        Event.ApprovalStatus requiredStatus = switch (level) {
            case L1_REVIEWER -> Event.ApprovalStatus.DRAFT;
            case L2_MANAGER -> Event.ApprovalStatus.L1_APPROVED;
            case L3_DIRECTOR -> Event.ApprovalStatus.L2_APPROVED;
        };
        return eventRepository.findByApprovalStatus(requiredStatus);
    }

    /**
     * Approve an event at a specific level.
     *
     * Workflow:
     *   DRAFT       + L1_REVIEWER approve → L1_APPROVED
     *   L1_APPROVED + L2_MANAGER  approve → L2_APPROVED
     *   L2_APPROVED + L3_DIRECTOR approve → LIVE (visible to all users)
     */
    @Transactional
    public Event approveEvent(UUID eventId, UUID approverUserId, String approverName,
                              EventApproval.ApprovalLevel level, String comment) {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        // Validate that the event is at the correct stage for this approval level
        validateApprovalOrder(event, level);

        // Record the approval action in the audit trail
        EventApproval approval = new EventApproval();
        approval.setEventId(eventId);
        approval.setApproverUserId(approverUserId);
        approval.setApproverName(approverName);
        approval.setLevel(level);
        approval.setAction(EventApproval.ApprovalAction.APPROVED);
        approval.setComment(comment);
        approvalRepository.save(approval);

        // Advance the event to the next stage
        Event.ApprovalStatus newStatus = switch (level) {
            case L1_REVIEWER -> Event.ApprovalStatus.L1_APPROVED;
            case L2_MANAGER -> Event.ApprovalStatus.L2_APPROVED;
            case L3_DIRECTOR -> Event.ApprovalStatus.LIVE;
        };

        event.setApprovalStatus(newStatus);
        eventRepository.save(event);

        log.info("Event '{}' [{}] approved at {} by {}. New status: {}",
                event.getTitle(), eventId, level, approverName, newStatus);

        // If the event just went LIVE, notify the business owner and all users
        if (newStatus == Event.ApprovalStatus.LIVE) {
            kafkaTemplate.send("notification.command.send",
                    String.format("{\"userId\":\"%s\", \"status\":\"SUCCESS\", \"message\":\"Your event '%s' has been approved and is now LIVE!\"}",
                            event.getCreatedByUserId(), event.getTitle()));

            kafkaTemplate.send("notification.command.send",
                    String.format("{\"channel\":\"ALL\", \"message\":\"New event available: '%s' by %s on %s\"}",
                            event.getTitle(), event.getOrganizationName(), event.getStartTime()));
        } else {
            // Notify that it's moved to the next level
            kafkaTemplate.send("notification.command.send",
                    String.format("{\"channel\":\"INTERNAL\", \"message\":\"Event '%s' passed %s review. Awaiting %s.\"}",
                            event.getTitle(), level, getNextLevel(level)));
        }

        return event;
    }

    /**
     * Reject an event at any level.
     * A rejection comment is required to explain the reason to the business owner.
     */
    @Transactional
    public Event rejectEvent(UUID eventId, UUID approverUserId, String approverName,
                             EventApproval.ApprovalLevel level, String rejectionReason) {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException("A rejection reason is required.");
        }

        // Record the rejection in the audit trail
        EventApproval approval = new EventApproval();
        approval.setEventId(eventId);
        approval.setApproverUserId(approverUserId);
        approval.setApproverName(approverName);
        approval.setLevel(level);
        approval.setAction(EventApproval.ApprovalAction.REJECTED);
        approval.setComment(rejectionReason);
        approvalRepository.save(approval);

        event.setApprovalStatus(Event.ApprovalStatus.REJECTED);
        eventRepository.save(event);

        log.warn("Event '{}' [{}] REJECTED at {} by {}. Reason: {}",
                event.getTitle(), eventId, level, approverName, rejectionReason);

        // Notify the business owner with the rejection reason
        kafkaTemplate.send("notification.command.send",
                String.format("{\"userId\":\"%s\", \"status\":\"FAILED\", \"message\":\"Your event '%s' was rejected at %s review. Reason: %s\"}",
                        event.getCreatedByUserId(), event.getTitle(), level, rejectionReason));

        return event;
    }

    /**
     * Get the full approval history for an event (audit trail).
     */
    public Map<String, Object> getApprovalHistory(UUID eventId) {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        List<EventApproval> history = approvalRepository.findByEventIdOrderByActionTimestampAsc(eventId);

        return Map.of(
                "event", event,
                "currentStatus", event.getApprovalStatus(),
                "approvalHistory", history,
                "totalApprovals", history.stream()
                        .filter(a -> a.getAction() == EventApproval.ApprovalAction.APPROVED).count()
        );
    }

    // =========================================================================
    // CUSTOMER: Browse & RSVP (only LIVE events)
    // =========================================================================

    /**
     * Get all LIVE events — only events that passed all 3 approval layers.
     */
    public List<Event> getLiveEvents() {
        return eventRepository.findByApprovalStatus(Event.ApprovalStatus.LIVE);
    }

    /**
     * RSVP to a live event.
     */
    @Transactional
    public EventAttendee rsvpToEvent(UUID eventId, UUID userId) {
        Objects.requireNonNull(eventId, "Event ID cannot be null");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        if (event.getApprovalStatus() != Event.ApprovalStatus.LIVE) {
            throw new IllegalArgumentException("Cannot RSVP — this event is not yet approved.");
        }

        if (attendeeRepository.existsByEventIdAndUserId(eventId, userId)) {
            throw new IllegalArgumentException("You have already RSVP'd to this event.");
        }

        if (event.getMaxCapacity() != null) {
            long currentCount = attendeeRepository.findByEventId(eventId).size();
            if (currentCount >= event.getMaxCapacity()) {
                throw new IllegalArgumentException("Event is at full capacity.");
            }
        }

        EventAttendee attendee = new EventAttendee();
        attendee.setEventId(eventId);
        attendee.setUserId(userId);
        attendee.setHasAttended(false);

        EventAttendee saved = attendeeRepository.save(attendee);
        log.info("User {} RSVP'd to event '{}'", userId, event.getTitle());

        kafkaTemplate.send("notification.command.send",
                String.format("{\"userId\":\"%s\", \"message\":\"You have RSVP'd to '%s'!\"}", userId, event.getTitle()));

        return saved;
    }

    /**
     * Mark attendance and award points.
     */
    @Transactional
    public EventAttendee markAttendance(UUID attendeeId) {
        Objects.requireNonNull(attendeeId, "Attendee ID cannot be null");
        EventAttendee attendee = attendeeRepository.findById(attendeeId)
                .orElseThrow(() -> new IllegalArgumentException("Attendee record not found."));
        attendee.setHasAttended(true);
        attendeeRepository.save(attendee);

        UUID eventId = attendee.getEventId();
        if (eventId == null) {
            throw new IllegalStateException("Attendee record has no associated event ID.");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        String payload = String.format(
                "{\"userId\":\"%s\", \"eventId\":\"%s\", \"pointsEarned\":%d, \"event\":\"EVENT_ATTENDED\"}",
                attendee.getUserId(), event.getId(), event.getAttendancePointsReward());
        kafkaTemplate.send("gamification.events", payload);

        log.info("User {} attended '{}'. Points awarded: {}",
                attendee.getUserId(), event.getTitle(), event.getAttendancePointsReward());
        return attendee;
    }

    // =========================================================================
    // Internal Helpers
    // =========================================================================

    /**
     * Enforces the strict approval order.
     * L1 can only act on DRAFT, L2 on L1_APPROVED, L3 on L2_APPROVED.
     */
    private void validateApprovalOrder(Event event, EventApproval.ApprovalLevel level) {
        Event.ApprovalStatus required = switch (level) {
            case L1_REVIEWER -> Event.ApprovalStatus.DRAFT;
            case L2_MANAGER -> Event.ApprovalStatus.L1_APPROVED;
            case L3_DIRECTOR -> Event.ApprovalStatus.L2_APPROVED;
        };

        if (event.getApprovalStatus() != required) {
            throw new IllegalArgumentException(
                    String.format("Cannot process %s approval. Event is currently at '%s', but must be at '%s'.",
                            level, event.getApprovalStatus(), required));
        }
    }

    private String getNextLevel(EventApproval.ApprovalLevel current) {
        return switch (current) {
            case L1_REVIEWER -> "L2 Manager";
            case L2_MANAGER -> "L3 Director";
            case L3_DIRECTOR -> "LIVE";
        };
    }
}
