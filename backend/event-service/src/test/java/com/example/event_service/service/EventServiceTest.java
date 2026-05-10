package com.example.event_service.service;

import com.example.event_service.model.Event;
import com.example.event_service.model.EventApproval;
import com.example.event_service.model.EventAttendee;
import com.example.event_service.repository.EventApprovalRepository;
import com.example.event_service.repository.EventAttendeeRepository;
import com.example.event_service.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventAttendeeRepository attendeeRepository;

    @Mock
    private EventApprovalRepository approvalRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private EventService eventService;

    private UUID userId;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
    }

    @Test
    void testSubmitEventProposal() {
        Event event = new Event();
        event.setTitle("Test Event");
        when(eventRepository.save(any(Event.class))).thenAnswer(i -> i.getArguments()[0]);

        Event result = eventService.submitEventProposal(event, userId, "Test Org");

        assertNotNull(result);
        assertEquals(Event.ApprovalStatus.DRAFT, result.getApprovalStatus());
        verify(kafkaTemplate).send(eq("notification.command.send"), anyString());
    }

    @Test
    void testApproveEvent_L1_Success() {
        Event event = new Event();
        event.setApprovalStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event result = eventService.approveEvent(eventId, userId, "Approver", EventApproval.ApprovalLevel.L1_REVIEWER, "L1 Good");

        assertEquals(Event.ApprovalStatus.L1_APPROVED, result.getApprovalStatus());
        verify(approvalRepository).save(any(EventApproval.class));
    }

    @Test
    void testApproveEvent_WrongOrder_ThrowsException() {
        Event event = new Event();
        event.setApprovalStatus(Event.ApprovalStatus.DRAFT);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // L2 cannot approve DRAFT
        assertThrows(IllegalArgumentException.class, () -> 
            eventService.approveEvent(eventId, userId, "Approver", EventApproval.ApprovalLevel.L2_MANAGER, "L2 Wait")
        );
    }

    @Test
    void testRejectEvent() {
        Event event = new Event();
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        Event result = eventService.rejectEvent(eventId, userId, "Approver", EventApproval.ApprovalLevel.L1_REVIEWER, "Bad");

        assertEquals(Event.ApprovalStatus.REJECTED, result.getApprovalStatus());
        verify(kafkaTemplate).send(eq("notification.command.send"), anyString());
    }

    @Test
    void testRsvpToEvent_Success() {
        Event event = new Event();
        event.setApprovalStatus(Event.ApprovalStatus.LIVE);
        event.setMaxCapacity(100);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(attendeeRepository.existsByEventIdAndUserId(eventId, userId)).thenReturn(false);
        when(attendeeRepository.save(any(EventAttendee.class))).thenAnswer(i -> i.getArguments()[0]);

        EventAttendee attendee = eventService.rsvpToEvent(eventId, userId);

        assertNotNull(attendee);
        verify(kafkaTemplate).send(eq("notification.command.send"), anyString());
    }

    @Test
    void testRsvpToEvent_FullCapacity_ThrowsException() {
        Event event = new Event();
        event.setApprovalStatus(Event.ApprovalStatus.LIVE);
        event.setMaxCapacity(1);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(attendeeRepository.findByEventId(eventId)).thenReturn(List.of(new EventAttendee()));

        assertThrows(IllegalArgumentException.class, () -> 
            eventService.rsvpToEvent(eventId, userId)
        );
    }

    @Test
    void testMarkAttendance() {
        UUID attendeeId = UUID.randomUUID();
        EventAttendee attendee = new EventAttendee();
        attendee.setUserId(userId);
        attendee.setEventId(eventId);
        
        Event event = new Event();
        event.setId(eventId);
        event.setAttendancePointsReward(200);

        when(attendeeRepository.findById(attendeeId)).thenReturn(Optional.of(attendee));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        EventAttendee result = eventService.markAttendance(attendeeId);

        assertTrue(result.isHasAttended());
        verify(kafkaTemplate).send(eq("gamification.events"), anyString());
    }
}
