package com.example.education_service.service;

import com.example.education_service.model.*;
import com.example.education_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class AdvisorServiceTest {

    @Mock private AdvisorProfileRepository profileRepo;
    @Mock private AdvisorSessionRepository sessionRepo;
    @Mock private AdvisorReviewRepository reviewRepo;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private AdvisorService advisorService;

    @Test
    void testRegisterAdvisor_Success() {
        UUID userId = UUID.randomUUID();
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(profileRepo.save(any())).thenReturn(new AdvisorProfile());

        AdvisorProfile result = advisorService.registerAdvisor(userId, "John", "Finance", "Bio");

        assertNotNull(result);
        verify(profileRepo).save(any());
    }

    @Test
    void testBookSession_Success() {
        UUID customerId = UUID.randomUUID();
        UUID advisorId = UUID.randomUUID();
        AdvisorProfile profile = new AdvisorProfile();
        profile.setId(advisorId);
        profile.setName("Advisor");
        profile.setPointsCost(1000);

        when(profileRepo.findById(advisorId)).thenReturn(Optional.of(profile));
        when(sessionRepo.save(any())).thenReturn(new AdvisorSession());

        AdvisorSession session = advisorService.bookSession(customerId, advisorId, LocalDateTime.now());

        assertNotNull(session);
        verify(kafkaTemplate, times(2)).send(anyString(), anyString());
    }

    @Test
    void testAdvisorCancelSession_Success() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setCustomerId(UUID.randomUUID());
        session.setPointsCharged(1000);
        session.setStatus(AdvisorSession.SessionStatus.BOOKED);

        when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

        advisorService.advisorCancelSession(sessionId, "Not available");

        assertEquals(AdvisorSession.SessionStatus.CANCELLED_BY_ADVISOR, session.getStatus());
        verify(kafkaTemplate, times(2)).send(anyString(), anyString());
    }

    @Test
    void testSubmitReview_Success() {
        UUID sessionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID advisorId = UUID.randomUUID();

        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setAdvisorId(advisorId);
        session.setStatus(AdvisorSession.SessionStatus.COMPLETED);

        AdvisorProfile profile = new AdvisorProfile();
        profile.setId(advisorId);

        when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));
        when(reviewRepo.existsBySessionId(sessionId)).thenReturn(false);
        when(profileRepo.findById(advisorId)).thenReturn(Optional.of(profile));
        when(reviewRepo.getAverageRatingByAdvisorId(advisorId)).thenReturn(4.5);
        when(reviewRepo.countByAdvisorId(advisorId)).thenReturn(10);

        AdvisorReview review = advisorService.submitReview(sessionId, customerId, 5, "Great!");

        assertNotNull(review);
        assertEquals(4.5, profile.getAverageRating());
        verify(reviewRepo).save(any());
    }

    @Test
    void testGetMyRatings_Success() {
        UUID advisorId = UUID.randomUUID();
        AdvisorProfile profile = new AdvisorProfile();
        profile.setId(advisorId);
        profile.setName("Advisor");
        profile.setAverageRating(4.5);

        when(profileRepo.findById(advisorId)).thenReturn(Optional.of(profile));
        when(reviewRepo.findByAdvisorIdOrderByCreatedAtDesc(advisorId)).thenReturn(java.util.List.of());

        Map<String, Object> result = advisorService.getMyRatings(advisorId);

        assertEquals("Advisor", result.get("advisorName"));
        assertEquals(4.5, result.get("averageRating"));
    }

    @Test
    void testCustomerCancelSession_Success() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setCustomerId(UUID.randomUUID());
        session.setStatus(AdvisorSession.SessionStatus.BOOKED);
        session.setPointsCharged(1000);

        when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

        advisorService.customerCancelSession(sessionId, "Change of plans");

        assertEquals(AdvisorSession.SessionStatus.CANCELLED_BY_CUSTOMER, session.getStatus());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testRescheduleSession_Success() {
        UUID sessionId = UUID.randomUUID();
        AdvisorSession session = new AdvisorSession();
        session.setId(sessionId);
        session.setCustomerId(UUID.randomUUID());

        when(sessionRepo.findById(sessionId)).thenReturn(Optional.of(session));

        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        advisorService.rescheduleSession(sessionId, newTime);

        assertEquals(AdvisorSession.SessionStatus.RESCHEDULED, session.getStatus());
        assertEquals(newTime, session.getScheduledAt());
    }

    @Test
    void testRegisterAdvisor_AlreadyExists() {
        UUID userId = UUID.randomUUID();
        when(profileRepo.findByUserId(userId)).thenReturn(Optional.of(new AdvisorProfile()));
        assertThrows(IllegalArgumentException.class, () -> advisorService.registerAdvisor(userId, "N", "S", "B"));
    }

    @Test
    void testSubmitReview_InvalidRating() {
        assertThrows(IllegalArgumentException.class, () -> advisorService.submitReview(UUID.randomUUID(), UUID.randomUUID(), 6, "Bad"));
    }
}
