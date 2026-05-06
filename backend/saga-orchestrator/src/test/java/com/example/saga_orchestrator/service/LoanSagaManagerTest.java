package com.example.saga_orchestrator.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.saga_orchestrator.model.SagaState;
import com.example.saga_orchestrator.repository.SagaStateRepository;

import java.util.Objects;
import com.example.saga_orchestrator.model.OutboxEvent;
import com.example.saga_orchestrator.repository.OutboxEventRepository;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration-style tests for the Saga Orchestrator.
 * Verifies:
 * 1. Correct Kafka topic wiring at each step
 * 2. State transitions are persisted
 * 3. Compensating transactions fire on failure
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class LoanSagaManagerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private SagaStateRepository sagaStateRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private LoanSagaManager sagaManager;

    private static final String PAYLOAD = "{\"claimId\":\"550e8400-e29b-41d4-a716-446655440000\", \"userId\":\"660e8400-e29b-41d4-a716-446655440001\", \"amount\":15000, \"type\":\"PERSONAL_LOAN\"}";

    @Test
    @DisplayName("Step 1: loan.requested → should persist state and send lock_funds command")
    void testStartSaga() {
        when(sagaStateRepository.save(any(SagaState.class))).thenAnswer(i -> {
            SagaState s = Objects.requireNonNull(i.getArgument(0));
            s.setSagaId(Objects.requireNonNull(UUID.randomUUID()));
            return s;
        });

        sagaManager.startLoanSaga(PAYLOAD);

        verify(sagaStateRepository).save(any(SagaState.class));
        verify(outboxEventRepository).save(argThat(event -> 
            event.getType().equals("contribution.command.lock_funds") && 
            event.getPayload().equals(PAYLOAD)));
    }

    @Test
    @DisplayName("Step 2 Success: funds_locked → should advance state and send payment command")
    void testFundsLocked() {
        SagaState state = new SagaState();
        state.setSagaId(Objects.requireNonNull(UUID.randomUUID()));
        when(sagaStateRepository.findByClaimId(any())).thenReturn(Optional.of(state));

        sagaManager.handleFundsLocked(PAYLOAD);

        verify(outboxEventRepository).save(argThat(event -> 
            event.getType().equals("payment.command.disburse") && 
            event.getPayload().equals(PAYLOAD)));
        verify(sagaStateRepository).save(any(SagaState.class));
    }

    @Test
    @DisplayName("Step 2 Failure: funds_lock_failed → should mark saga as FAILED")
    void testFundsLockFailed() {
        SagaState state = new SagaState();
        state.setSagaId(Objects.requireNonNull(UUID.randomUUID()));
        when(sagaStateRepository.findByClaimId(any())).thenReturn(Optional.of(state));

        sagaManager.handleFundsLockFailed(PAYLOAD);

        verify(outboxEventRepository).save(argThat(event -> 
            event.getType().equals("notification.command.send")));
        verify(sagaStateRepository).save(argThat(s -> s.getStatus() == SagaState.SagaStatus.FAILED));
    }

    @Test
    @DisplayName("Step 3 Success: payment_disbursed → should complete saga and notify")
    void testPaymentSuccess() {
        SagaState state = new SagaState();
        state.setSagaId(Objects.requireNonNull(UUID.randomUUID()));
        when(sagaStateRepository.findByClaimId(any())).thenReturn(Optional.of(state));

        sagaManager.handlePaymentSuccess(PAYLOAD);

        verify(outboxEventRepository, times(3)).save(any(OutboxEvent.class));
        verify(sagaStateRepository).save(argThat(s -> s.getStatus() == SagaState.SagaStatus.COMPLETED));
    }

    @Test
    @DisplayName("Step 3 Failure: payment_failed → should trigger compensating unlock")
    void testPaymentFailure() {
        SagaState state = new SagaState();
        state.setSagaId(Objects.requireNonNull(UUID.randomUUID()));
        when(sagaStateRepository.findByClaimId(any())).thenReturn(Optional.of(state));

        sagaManager.handlePaymentFailure(PAYLOAD);

        verify(outboxEventRepository, times(3)).save(any(OutboxEvent.class));
        verify(sagaStateRepository).save(argThat(s -> s.getStatus() == SagaState.SagaStatus.COMPENSATED));
    }
}
