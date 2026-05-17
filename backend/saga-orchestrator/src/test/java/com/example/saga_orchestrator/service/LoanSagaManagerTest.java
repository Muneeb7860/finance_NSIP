package com.example.saga_orchestrator.service;

import com.example.saga_orchestrator.model.SagaState;
import com.example.saga_orchestrator.repository.OutboxEventRepository;
import com.example.saga_orchestrator.repository.SagaStateRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class LoanSagaManagerTest {

    @Mock
    private SagaStateRepository sagaStateRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LoanSagaManager loanSagaManager;

    @Test
    void testStartLoanSaga_Success() {
        UUID claimId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String payload = "{\"claimId\":\"" + claimId + "\", \"userId\":\"" + userId + "\"}";

        when(sagaStateRepository.findByClaimId(claimId)).thenReturn(Optional.empty());
        when(sagaStateRepository.save(any(SagaState.class))).thenAnswer(i -> {
            SagaState s = (SagaState) i.getArguments()[0];
            s.setSagaId(UUID.randomUUID());
            return s;
        });

        loanSagaManager.startLoanSaga(payload);

        verify(sagaStateRepository).save(any(SagaState.class));
        verify(outboxEventRepository).save(any());
    }

    @Test
    void testHandleFundsLocked() {
        UUID claimId = UUID.randomUUID();
        String payload = "{\"claimId\":\"" + claimId + "\"}";
        SagaState state = new SagaState();
        state.setSagaId(UUID.randomUUID());

        when(sagaStateRepository.findByClaimId(claimId)).thenReturn(Optional.of(state));

        loanSagaManager.handleFundsLocked(payload);

        assertEquals(SagaState.SagaStep.PAYMENT_REQUESTED, state.getCurrentStep());
        verify(sagaStateRepository).save(state);
    }

    @Test
    void testHandleFundsLockFailed() {
        UUID claimId = UUID.randomUUID();
        String payload = "{\"claimId\":\"" + claimId + "\"}";
        SagaState state = new SagaState();
        state.setSagaId(UUID.randomUUID());

        when(sagaStateRepository.findByClaimId(claimId)).thenReturn(Optional.of(state));

        loanSagaManager.handleFundsLockFailed(payload);

        assertEquals(SagaState.SagaStatus.FAILED, state.getStatus());
        verify(sagaStateRepository).save(state);
    }

    @Test
    void testHandlePaymentSuccess() {
        UUID claimId = UUID.randomUUID();
        String payload = "{\"claimId\":\"" + claimId + "\"}";
        SagaState state = new SagaState();
        state.setSagaId(UUID.randomUUID());

        when(sagaStateRepository.findByClaimId(claimId)).thenReturn(Optional.of(state));

        loanSagaManager.handlePaymentSuccess(payload);

        assertEquals(SagaState.SagaStatus.COMPLETED, state.getStatus());
        verify(outboxEventRepository, times(3)).save(any());
    }

    @Test
    void testHandlePaymentFailure() {
        UUID claimId = UUID.randomUUID();
        String payload = "{\"claimId\":\"" + claimId + "\"}";
        SagaState state = new SagaState();
        state.setSagaId(UUID.randomUUID());

        when(sagaStateRepository.findByClaimId(claimId)).thenReturn(Optional.of(state));

        loanSagaManager.handlePaymentFailure(payload);

        assertEquals(SagaState.SagaStatus.COMPENSATED, state.getStatus());
        verify(outboxEventRepository, times(3)).save(any());
    }
}
