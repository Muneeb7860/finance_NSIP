package com.example.contribution_service.service;

import com.example.contribution_service.model.FundLock;
import com.example.contribution_service.repository.ContributionRepository;
import com.example.contribution_service.repository.FundLockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class FundLockServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ContributionRepository contributionRepository;

    @Mock
    private FundLockRepository fundLockRepository;

    @InjectMocks
    private FundLockService fundLockService;

    @Test
    void testHandleLockFunds_Success() {
        UUID userId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();
        String payload = "{\"userId\":\"" + userId + "\", \"claimId\":\"" + claimId + "\", \"amount\":1000, \"type\":\"PERSONAL_LOAN\"}";

        when(contributionRepository.getFirstContributionDate(userId))
                .thenReturn(Optional.of(LocalDateTime.now().minusYears(5)));
        when(contributionRepository.getTotalSavingsByUserId(userId)).thenReturn(new BigDecimal("10000"));
        when(fundLockRepository.getTotalLockedByUserId(userId)).thenReturn(BigDecimal.ZERO);

        fundLockService.handleLockFunds(payload);

        verify(fundLockRepository).save(any(FundLock.class));
        verify(kafkaTemplate).send(eq("contribution.event.funds_locked"), eq(payload));
    }

    @Test
    void testHandleLockFunds_VestingFail() {
        UUID userId = UUID.randomUUID();
        String payload = "{\"userId\":\"" + userId + "\", \"amount\":1000}";

        when(contributionRepository.getFirstContributionDate(userId))
                .thenReturn(Optional.of(LocalDateTime.now().minusYears(1)));

        fundLockService.handleLockFunds(payload);

        verify(kafkaTemplate).send(eq("contribution.event.funds_lock_failed"), eq(payload));
    }

    @Test
    void testHandleLockFunds_CreditLimitFail() {
        UUID userId = UUID.randomUUID();
        String payload = "{\"userId\":\"" + userId + "\", \"amount\":5000, \"type\":\"PERSONAL_LOAN\"}";

        when(contributionRepository.getFirstContributionDate(userId))
                .thenReturn(Optional.of(LocalDateTime.now().minusYears(5)));
        when(contributionRepository.getTotalSavingsByUserId(userId)).thenReturn(new BigDecimal("10000"));
        when(fundLockRepository.getTotalLockedByUserId(userId)).thenReturn(BigDecimal.ZERO);
        // Limit is 3000

        fundLockService.handleLockFunds(payload);

        verify(kafkaTemplate).send(eq("contribution.event.funds_lock_failed"), eq(payload));
    }

    @Test
    void testHandleUnlockFunds() {
        UUID claimId = UUID.randomUUID();
        String payload = "{\"claimId\":\"" + claimId + "\"}";
        FundLock lock = new FundLock();
        when(fundLockRepository.findByClaimId(claimId)).thenReturn(Optional.of(lock));

        fundLockService.handleUnlockFunds(payload);

        verify(fundLockRepository).save(lock);
    }
}
