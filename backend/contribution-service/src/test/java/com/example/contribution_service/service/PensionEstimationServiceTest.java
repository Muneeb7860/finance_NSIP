package com.example.contribution_service.service;

import com.example.contribution_service.repository.ContributionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class PensionEstimationServiceTest {

    @Mock private ContributionRepository contributionRepository;

    @InjectMocks
    private PensionEstimationService pensionEstimationService;

    @Test
    void testCalculateEstimate() {
        UUID userId = UUID.randomUUID();
        when(contributionRepository.getTotalSavingsByUserId(userId)).thenReturn(new BigDecimal("10000"));

        PensionEstimationService.PensionEstimate estimate = 
                pensionEstimationService.calculateEstimate(userId, 30, new BigDecimal("5000"));

        assertNotNull(estimate);
        assertTrue(estimate.estimatedMonthlyPension().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(30, estimate.yearsToRetirement());
        verify(contributionRepository).getTotalSavingsByUserId(userId);
    }
}
