package com.example.rewards_service.service;

import com.example.rewards_service.model.AdvisorSession;
import com.example.rewards_service.repository.AdvisorSessionRepository;
import com.example.rewards_service.repository.CertificateRepository;
import com.example.rewards_service.repository.PointsLedgerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class RewardsServiceTest {

    @Mock private PointsLedgerRepository pointsLedgerRepository;
    @Mock private AdvisorSessionRepository advisorSessionRepository;
    @Mock private CertificateRepository certificateRepository;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @Spy private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RewardsService rewardsService;

    @Test
    void testHandleGamificationEvent_CourseCompleted() {
        String payload = "{\"userId\":\"" + UUID.randomUUID() + "\", \"eventType\":\"COURSE_COMPLETED\"}";
        rewardsService.handleGamificationEvent(payload);
        verify(pointsLedgerRepository).save(any());
    }

    @Test
    void testAwardPoints_Success() {
        UUID userId = UUID.randomUUID();
        rewardsService.awardPoints(userId, 100, "Test");
        verify(pointsLedgerRepository).save(any());
    }

    @Test
    void testBookSession_InsufficientPoints() {
        UUID userId = UUID.randomUUID();
        when(pointsLedgerRepository.getTotalPointsByUserId(userId)).thenReturn(500);
        assertThrows(IllegalArgumentException.class, () -> rewardsService.bookSession(userId, UUID.randomUUID(), LocalDateTime.now()));
    }

    @Test
    void testBookSession_Success() {
        UUID userId = UUID.randomUUID();
        UUID advisorId = UUID.randomUUID();
        when(pointsLedgerRepository.getTotalPointsByUserId(userId)).thenReturn(1500);
        when(advisorSessionRepository.save(any())).thenReturn(new AdvisorSession());

        rewardsService.bookSession(userId, advisorId, LocalDateTime.now());

        verify(pointsLedgerRepository).save(any());
        verify(advisorSessionRepository).save(any());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testCancelSession_OutsideWindow_Refund() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setScheduledTime(LocalDateTime.now().plusDays(2));
        session.setStatus(AdvisorSession.SessionStatus.SCHEDULED);

        when(advisorSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        rewardsService.cancelSession(sessionId);

        assertEquals(AdvisorSession.SessionStatus.CANCELED, session.getStatus());
        verify(pointsLedgerRepository).save(any()); // Refund saved
    }

    @Test
    void testCancelSession_WithinWindow_NoRefund() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setScheduledTime(LocalDateTime.now().plusHours(1));
        session.setStatus(AdvisorSession.SessionStatus.SCHEDULED);

        when(advisorSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        rewardsService.cancelSession(sessionId);

        assertEquals(AdvisorSession.SessionStatus.CANCELED, session.getStatus());
        verify(pointsLedgerRepository, never()).save(any()); // No refund
    }

    @Test
    void testGetLeaderboard() {
        List<Object[]> mockData = Collections.singletonList(new Object[]{UUID.randomUUID(), 1000L});
        when(pointsLedgerRepository.getLeaderboard()).thenReturn(mockData);
        
        List<?> result = rewardsService.getLeaderboard(10);
        assertFalse(result.isEmpty());
    }

    @Test
    void testHandleGamificationEvent_UnknownEvent() {
        String payload = "{\"userId\":\"" + UUID.randomUUID() + "\", \"eventType\":\"UNKNOWN\"}";
        rewardsService.handleGamificationEvent(payload);
        verify(pointsLedgerRepository, never()).save(any());
    }

    @Test
    void testHandleGamificationEvent_MalformedJson() {
        String payload = "{invalid}";
        rewardsService.handleGamificationEvent(payload);
        verify(pointsLedgerRepository, never()).save(any());
    }

    @Test
    void testRescheduleSession_InvalidStatus() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setStatus(AdvisorSession.SessionStatus.CANCELED);

        when(advisorSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(IllegalArgumentException.class, () -> rewardsService.rescheduleSession(sessionId, LocalDateTime.now()));
    }

    @Test
    void testCancelSession_AlreadyCompleted() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setStatus(AdvisorSession.SessionStatus.COMPLETED);

        when(advisorSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));

        assertThrows(IllegalArgumentException.class, () -> rewardsService.cancelSession(sessionId));
    }
}
