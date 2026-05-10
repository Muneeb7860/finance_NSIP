package com.example.claim_service.domain.service;

import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.application.port.out.ClaimReviewerPort;
import com.example.claim_service.domain.model.Claim;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class ClaimDomainServiceTest {

    @Mock private ClaimRepositoryPort repository;
    @Mock private ClaimReviewerPort reviewer;

    @InjectMocks
    private ClaimDomainService claimDomainService;

    @Test
    void testSubmitClaim_WithAiSuccess() {
        Claim claim = new Claim();
        claim.setUserId("user-1");
        claim.setType(Claim.ClaimType.PERSONAL_LOAN);

        when(repository.save(any())).thenReturn(claim);
        when(reviewer.review(any())).thenReturn(new String[]{"APPROVED", "Reasoning..."});

        Claim result = claimDomainService.submitClaim(claim);

        assertNotNull(result);
        assertEquals("APPROVED", result.getAiRecommendation());
        verify(reviewer).review(any());
        verify(repository, times(2)).save(any());
    }

    @Test
    void testSubmitClaim_WithAiFailure() {
        Claim claim = new Claim();
        claim.setUserId("user-1");
        claim.setType(Claim.ClaimType.EMERGENCY_RELIEF);

        when(repository.save(any())).thenReturn(claim);
        when(reviewer.review(any())).thenThrow(new RuntimeException("AI Down"));

        Claim result = claimDomainService.submitClaim(claim);

        assertNotNull(result);
        assertNull(result.getAiRecommendation());
        verify(repository, times(1)).save(any());
    }

    @Test
    void testUpdateClaimStatus_NotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> claimDomainService.updateClaimStatus("123", Claim.ClaimStatus.APPROVED));
    }

    @Test
    void testGetClaimsByUserId() {
        when(repository.findByUserId("user-1")).thenReturn(java.util.List.of(new Claim()));
        var result = claimDomainService.getClaimsByUserId("user-1");
        assertFalse(result.isEmpty());
    }
}
