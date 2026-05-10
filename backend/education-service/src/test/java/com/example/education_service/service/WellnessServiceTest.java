package com.example.education_service.service;

import com.example.education_service.model.WellnessRegistration;
import com.example.education_service.repository.WellnessRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class WellnessServiceTest {

    @Mock private WellnessRegistrationRepository repository;

    @InjectMocks
    private WellnessService wellnessService;

    @Test
    void testRegisterCondition() {
        String userId = "user-1";
        when(repository.save(any(WellnessRegistration.class))).thenAnswer(i -> i.getArguments()[0]);

        WellnessRegistration result = wellnessService.registerCondition(userId, "Chronic", "Medical");

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("PENDING_APPROVAL", result.getStatus());
        verify(repository).save(any(WellnessRegistration.class));
    }

    @Test
    void testGetUserRegistrations() {
        String userId = "user-1";
        when(repository.findByUserId(userId)).thenReturn(List.of(new WellnessRegistration()));

        List<WellnessRegistration> result = wellnessService.getUserRegistrations(userId);

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateVisit() {
        Long regId = 1L;
        WellnessRegistration reg = new WellnessRegistration();
        when(repository.findById(regId)).thenReturn(Optional.of(reg));

        wellnessService.updateVisit(regId);

        assertNotNull(reg.getLastVisitAt());
        verify(repository).save(reg);
    }
}
