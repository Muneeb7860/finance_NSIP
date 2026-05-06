package com.example.claim_service.domain.service;

import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.application.port.out.ClaimReviewerPort;
import com.example.claim_service.domain.model.Claim;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimDomainServiceTest {

    @Mock
    private ClaimRepositoryPort repository;

    @Mock
    private ClaimReviewerPort reviewer;

    private ClaimDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new ClaimDomainService(repository, reviewer);
    }

    @Test
    @DisplayName("Submit a valid personal loan claim")
    void testSubmitValidClaim() {
        String userId = UUID.randomUUID().toString();
        Claim claim = new Claim();
        claim.setUserId(userId);
        claim.setType(Claim.ClaimType.PERSONAL_LOAN);
        claim.setAmount(new BigDecimal("1000"));

        when(repository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));
        when(reviewer.review(any(Claim.class))).thenReturn(new String[]{"APPROVE", "Looks good"});

        Claim result = domainService.submitClaim(claim);

        assertNotNull(result);
        assertEquals(Claim.ClaimStatus.PENDING, result.getStatus());
        verify(repository).save(any(Claim.class));
    }

    @Test
    @DisplayName("Update claim status to approved")
    void testUpdateStatus() {
        String claimId = UUID.randomUUID().toString();
        Claim existing = new Claim();
        existing.setId(claimId);
        existing.setStatus(Claim.ClaimStatus.PENDING);

        when(repository.findById(claimId)).thenReturn(Optional.of(existing));
        when(repository.save(any(Claim.class))).thenAnswer(i -> i.getArgument(0));

        Claim result = domainService.updateClaimStatus(claimId, Claim.ClaimStatus.APPROVED);

        assertEquals(Claim.ClaimStatus.APPROVED, result.getStatus());
    }

    @Test
    @DisplayName("Throw exception if claim not found during status update")
    void testUpdateStatusNotFound() {
        String claimId = "invalid-id";
        when(repository.findById(claimId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            domainService.updateClaimStatus(claimId, Claim.ClaimStatus.APPROVED)
        );
    }
}
