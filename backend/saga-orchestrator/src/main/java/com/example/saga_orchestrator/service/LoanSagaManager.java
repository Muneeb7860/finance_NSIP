package com.example.saga_orchestrator.service;

import com.example.saga_orchestrator.model.SagaState;
import com.example.saga_orchestrator.model.OutboxEvent;
import com.example.saga_orchestrator.repository.SagaStateRepository;
import com.example.saga_orchestrator.repository.OutboxEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saga Orchestrator for Loan Disbursement.
 */
@Service
@Slf4j
public class LoanSagaManager {

    @Autowired
    private SagaStateRepository sagaStateRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    /**
     * SAGA STEP 1: Start the saga from a loan request.
     */
    @KafkaListener(topics = "loan.requested", groupId = "saga-group")
    @Transactional
    public void startLoanSaga(String payload) {
        log.info("SAGA: Starting Loan Disbursement Saga. Payload: {}", payload);

        UUID claimId = extractUUID(payload, "claimId");
        UUID userId = extractUUID(payload, "userId");

        if (sagaStateRepository.findByClaimId(claimId).isPresent()) {
            log.warn("SAGA: Duplicate loan request for claimId: {}. Skipping.", claimId);
            return;
        }

        SagaState state = new SagaState();
        state.setClaimId(claimId);
        state.setUserId(userId);
        state.setCurrentStep(SagaState.SagaStep.FUNDS_LOCK_REQUESTED);
        state.setStatus(SagaState.SagaStatus.RUNNING);
        sagaStateRepository.save(state);

        log.info("SAGA [{}]: Persisted as FUNDS_LOCK_REQUESTED", state.getSagaId());
        saveOutboxEvent(state.getSagaId(), "contribution.command.lock_funds", payload);
    }

    /**
     * SAGA STEP 2 (SUCCESS): Funds locked.
     */
    @KafkaListener(topics = "contribution.event.funds_locked", groupId = "saga-group")
    @Transactional
    public void handleFundsLocked(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Saga not found for claim: " + claimId));

        state.setCurrentStep(SagaState.SagaStep.PAYMENT_REQUESTED);
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.info("SAGA [{}]: Funds locked. Moving to PAYMENT_REQUESTED", state.getSagaId());
        saveOutboxEvent(state.getSagaId(), "payment.command.disburse", payload);
    }

    /**
     * SAGA STEP 2 (FAILURE): Funds lock failed.
     */
    @KafkaListener(topics = "contribution.event.funds_lock_failed", groupId = "saga-group")
    @Transactional
    public void handleFundsLockFailed(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Saga not found for claim: " + claimId));

        state.setStatus(SagaState.SagaStatus.FAILED);
        state.setFailureReason("Fund lock failed.");
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.warn("SAGA [{}]: FAILED at fund lock step.", state.getSagaId());
        saveOutboxEvent(state.getSagaId(), "notification.command.send", "{\"status\": \"FAILED\"}");
    }

    /**
     * SAGA STEP 3 (SUCCESS): Payment completed.
     */
    @KafkaListener(topics = "payment.event.disbursed", groupId = "saga-group")
    @Transactional
    public void handlePaymentSuccess(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Saga not found for claim: " + claimId));

        state.setCurrentStep(SagaState.SagaStep.PAYMENT_COMPLETED);
        state.setStatus(SagaState.SagaStatus.COMPLETED);
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.info("SAGA [{}]: COMPLETED successfully.", state.getSagaId());
        
        // 3 Events required by unit tests
        saveOutboxEvent(state.getSagaId(), "claim.command.complete", payload);
        saveOutboxEvent(state.getSagaId(), "notification.command.send", "{\"status\": \"SUCCESS\"}");
        saveOutboxEvent(state.getSagaId(), "review.command.prompt", payload);
    }

    /**
     * SAGA STEP 3 (FAILURE): Payment failed. Trigger compensation.
     */
    @KafkaListener(topics = "payment.event.failed", groupId = "saga-group")
    @Transactional
    public void handlePaymentFailure(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new RuntimeException("Saga not found for claim: " + claimId));

        state.setCurrentStep(SagaState.SagaStep.COMPENSATING_UNLOCK);
        state.setStatus(SagaState.SagaStatus.COMPENSATED);
        state.setFailureReason("Payment failed.");
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.warn("SAGA [{}]: Payment FAILED. Unlocking funds.", state.getSagaId());

        // 3 Events required by unit tests
        saveOutboxEvent(state.getSagaId(), "contribution.command.unlock_funds", payload);
        saveOutboxEvent(state.getSagaId(), "claim.command.fail", payload);
        saveOutboxEvent(state.getSagaId(), "notification.command.send", "{\"status\": \"FAILED\"}");
    }

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private void saveOutboxEvent(UUID sagaId, String topic, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("LoanSaga");
        event.setAggregateId(sagaId.toString());
        event.setType(topic);
        event.setPayload(payload);
        outboxEventRepository.save(event);
    }

    private UUID extractUUID(String json, String key) {
        try {
            return UUID.fromString(objectMapper.readTree(json).get(key).asText());
        } catch (Exception e) {
            log.error("Failed to extract {} from JSON: {}", key, e.getMessage());
            return UUID.randomUUID();
        }
    }
}
