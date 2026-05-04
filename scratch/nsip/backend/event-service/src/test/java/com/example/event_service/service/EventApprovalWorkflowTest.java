package com.example.event_service.service;

import com.example.event_service.model.Event;
import com.example.event_service.model.EventApproval;
import com.example.event_service.repository.EventApprovalRepository;
import com.example.event_service.repository.EventAttendeeRepository;
import com.example.event_service.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class EventApprovalWorkflowTest {

    @Mock private EventRepository eventRepository;
    @Mock private EventApprovalRepository approvalRepository;
    @Mock private EventAttendeeRepository attendeeRepository;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks private EventService eventService;

    @org.springframework.lang.NonNull
    private UUID eventId = Objects.requireNonNull(UUID.randomUUID());
    @org.springframework.lang.NonNull
    private UUID adminId = Objects.requireNonNull(UUID.randomUUID());

    @BeforeEach
    void setUp() {
        // IDs are already initialized
    }

    private Event createEventAtStatus(Event.ApprovalStatus status) {
        Event event = new Event();
        event.setId(eventId);
        event.setTitle("Ramadan Charity Run");
        event.setCategory(Event.EventCategory.RAMADAN_EVENT);
        event.setType(Event.EventType.PHYSICAL);
        event.setApprovalStatus(status);
        event.setCreatedByUserId(UUID.randomUUID());
        event.setOrganizationName("TechCorp LLC");
        return event;
    }

    // --- Happy Path: Full approval chain ---

    @Test
    @DisplayName("L1 Reviewer can approve a DRAFT event → becomes L1_APPROVED")
    void testL1Approval() {
        Event event = createEventAtStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Event result = eventService.approveEvent(eventId, adminId, "Alice Reviewer",
                EventApproval.ApprovalLevel.L1_REVIEWER, "Looks good");

        assertEquals(Event.ApprovalStatus.L1_APPROVED, result.getApprovalStatus());
        verify(approvalRepository).save(any(EventApproval.class));
    }

    @Test
    @DisplayName("L2 Manager can approve an L1_APPROVED event → becomes L2_APPROVED")
    void testL2Approval() {
        Event event = createEventAtStatus(Event.ApprovalStatus.L1_APPROVED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Event result = eventService.approveEvent(eventId, adminId, "Bob Manager",
                EventApproval.ApprovalLevel.L2_MANAGER, "Budget approved");

        assertEquals(Event.ApprovalStatus.L2_APPROVED, result.getApprovalStatus());
    }

    @Test
    @DisplayName("L3 Director can approve an L2_APPROVED event → becomes LIVE")
    void testL3ApprovalGoesLive() {
        Event event = createEventAtStatus(Event.ApprovalStatus.L2_APPROVED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Event result = eventService.approveEvent(eventId, adminId, "Carol Director",
                EventApproval.ApprovalLevel.L3_DIRECTOR, "Approved for publication");

        assertEquals(Event.ApprovalStatus.LIVE, result.getApprovalStatus());
        // Should send notification to business owner AND broadcast to all users
        verify(kafkaTemplate, atLeast(2)).send(eq("notification.command.send"), anyString());
    }

    // --- Order Enforcement ---

    @Test
    @DisplayName("L2 Manager CANNOT approve a DRAFT event (must be L1_APPROVED first)")
    void testL2CannotSkipL1() {
        Event event = createEventAtStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.approveEvent(eventId, adminId, "Bob Manager",
                        EventApproval.ApprovalLevel.L2_MANAGER, "Trying to skip"));
    }

    @Test
    @DisplayName("L3 Director CANNOT approve a DRAFT event (must pass L1 and L2 first)")
    void testL3CannotSkipL1L2() {
        Event event = createEventAtStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.approveEvent(eventId, adminId, "Carol Director",
                        EventApproval.ApprovalLevel.L3_DIRECTOR, "Trying to skip"));
    }

    // --- Rejection ---

    @Test
    @DisplayName("L1 Reviewer can reject a DRAFT event")
    void testL1Rejection() {
        Event event = createEventAtStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Event result = eventService.rejectEvent(eventId, adminId, "Alice Reviewer",
                EventApproval.ApprovalLevel.L1_REVIEWER, "Inappropriate content");

        assertEquals(Event.ApprovalStatus.REJECTED, result.getApprovalStatus());
    }

    @Test
    @DisplayName("Rejection without a reason is not allowed")
    void testRejectionRequiresReason() {
        Event event = createEventAtStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.rejectEvent(eventId, adminId, "Alice Reviewer",
                        EventApproval.ApprovalLevel.L1_REVIEWER, ""));
    }

    // --- RSVP Guard ---

    @Test
    @DisplayName("Users CANNOT RSVP to events that are not LIVE")
    void testCannotRsvpToNonLiveEvent() {
        Event event = createEventAtStatus(Event.ApprovalStatus.L1_APPROVED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class, () ->
                eventService.rsvpToEvent(eventId, UUID.randomUUID()));
    }
}
