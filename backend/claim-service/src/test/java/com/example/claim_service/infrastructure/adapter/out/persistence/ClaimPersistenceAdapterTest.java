package com.example.claim_service.infrastructure.adapter.out.persistence;

import com.example.claim_service.domain.model.Claim;
import com.example.claim_service.repository.ClaimRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class ClaimPersistenceAdapterTest {

    @Mock private ClaimRepository jpaRepository;

    @InjectMocks
    private ClaimPersistenceAdapter adapter;

    @Test
    void testSave() {
        Claim domain = new Claim();
        domain.setUserId(UUID.randomUUID().toString());
        domain.setAmount(new BigDecimal("100"));
        domain.setType(Claim.ClaimType.EMERGENCY_RELIEF);
        domain.setStatus(Claim.ClaimStatus.PENDING);

        com.example.claim_service.model.Claim jpa = new com.example.claim_service.model.Claim();
        jpa.setId(UUID.randomUUID());
        jpa.setUserId(UUID.fromString(domain.getUserId()));
        jpa.setAmount(domain.getAmount());
        jpa.setClaimType(com.example.claim_service.model.Claim.ClaimType.EMERGENCY_RELIEF);
        jpa.setStatus(com.example.claim_service.model.Claim.ClaimStatus.PENDING);

        when(jpaRepository.save(any())).thenReturn(jpa);

        Claim result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(jpa.getId().toString(), result.getId());
        verify(jpaRepository).save(any());
    }
}
