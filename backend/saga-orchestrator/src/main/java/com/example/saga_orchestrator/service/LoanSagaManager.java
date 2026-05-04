package com.example.saga_orchestrator.service;

import com.example.saga_orchestrator.model.SagaState;
import com.example.saga_orchestrator.repository.SagaStateRepository;
import com.example.saga_orchestrator.model.OutboxEvent;
import com.example.saga_orchestrator.repository.OutboxEventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Saga Orchestrator with persistent state.
 *
 * Every step transition is persisted to the saga_state table BEFORE sending
 * the next Kafka command. This ensures that if the pod crashes between steps,
 * we know exactly where the saga left off and can either resume or compensate.
 *
 * Recovery logic (not yet implemented) would:
 * 1. Query for sagas with status=RUNNING on startup
 * 2. Check the currentStep to determine what to retry or compensate
 */
@Service
@Slf4j
public class LoanSagaManager {

    @Autowired
    private SagaStateRepository sagaStateRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    private void saveOutboxEvent(UUID sagaId, String topic, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("LoanSaga");
        event.setAggregateId(sagaId.toString());
        event.setType(topic);
        event.setPayload(payload);
        outboxEventRepository.save(event);
    }

    /**
     * SAGA STEP 1: A new Loan Request arrives.
     * Create a persistent saga record, then command the Contribution Service to lock funds.
     */
    @KafkaListener(topics = "loan.requested", groupId = "saga-group")
    @Transactional
    public void startLoanSaga(String payload) {
        log.info("SAGA: Starting Loan Disbursement Saga. Payload: {}", payload);

        // Parse claimId and userId from payload (simplified — use Jackson in production)
        UUID claimId = extractUUID(payload, "claimId");
        UUID userId = extractUUID(payload, "userId");

        // Persist saga state: INITIATED → FUNDS_LOCK_REQUESTED
        SagaState state = new SagaState();
        state.setClaimId(claimId);
        state.setUserId(userId);
        state.setCurrentStep(SagaState.SagaStep.FUNDS_LOCK_REQUESTED);
        state.setStatus(SagaState.SagaStatus.RUNNING);
        sagaStateRepository.save(state);

        log.info("SAGA [{}]: State persisted as FUNDS_LOCK_REQUESTED", state.getSagaId());
        saveOutboxEvent(state.getSagaId(), "contribution.command.lock_funds", payload);
    }

    /**
     * SAGA STEP 2 (SUCCESS): Funds locked. Advance to payment.
     */
    @KafkaListener(topics = "contribution.event.funds_locked", groupId = "saga-group")
    @Transactional
    public void handleFundsLocked(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new IllegalStateException("No saga found for claim: " + claimId));

        state.setCurrentStep(SagaState.SagaStep.PAYMENT_REQUESTED);
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.info("SAGA [{}]: Funds locked. State advanced to PAYMENT_REQUESTED", state.getSagaId());
        saveOutboxEvent(state.getSagaId(), "payment.command.disburse", payload);
    }

    /**
     * SAGA STEP 2 (FAILURE): Funds lock failed (vesting period not met, insufficient balance).
     */
    @KafkaListener(topics = "contribution.event.funds_lock_failed", groupId = "saga-group")
    @Transactional
    public void handleFundsLockFailed(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new IllegalStateException("No saga found for claim: " + claimId));

        state.setStatus(SagaState.SagaStatus.FAILED);
        state.setFailureReason("Fund lock failed: vesting period not met or insufficient balance.");
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.warn("SAGA [{}]: FAILED at fund lock step.", state.getSagaId());
        saveOutboxEvent(state.getSagaId(), "notification.command.send",
                "{\"status\": \"FAILED\", \"reason\": \"Insufficient vested funds or 3-year limit not met.\"}");
    }

    /**
     * SAGA STEP 3 (SUCCESS): Payment disbursed. Saga completes.
     */
    @KafkaListener(topics = "payment.event.disbursed", groupId = "saga-group")
    @Transactional
    public void handlePaymentSuccess(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new IllegalStateException("No saga found for claim: " + claimId));

        state.setCurrentStep(SagaState.SagaStep.PAYMENT_COMPLETED);
        state.setStatus(SagaState.SagaStatus.COMPLETED);
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.info("SAGA [{}]: COMPLETED successfully!", state.getSagaId());

        saveOutboxEvent(state.getSagaId(), "claim.command.complete", payload);
        saveOutboxEvent(state.getSagaId(), "notification.command.send",
                "{\"status\": \"SUCCESS\", \"message\": \"Loan approved and disbursed to your wallet!\"}");
        saveOutboxEvent(state.getSagaId(), "review.command.prompt", payload);
    }

    /**
     * SAGA STEP 3 (FAILURE): Payment failed. Trigger compensating transaction.
     */
    @KafkaListener(topics = "payment.event.failed", groupId = "saga-group")
    @Transactional
    public void handlePaymentFailure(String payload) {
        UUID claimId = extractUUID(payload, "claimId");
        SagaState state = sagaStateRepository.findByClaimId(claimId)
                .orElseThrow(() -> new IllegalStateException("No saga found for claim: " + claimId));

        state.setCurrentStep(SagaState.SagaStep.COMPENSATING_UNLOCK);
        state.setStatus(SagaState.SagaStatus.COMPENSATED);
        state.setFailureReason("Payment gateway failed. Compensating transaction triggered.");
        state.setUpdatedAt(LocalDateTime.now());
        sagaStateRepository.save(state);

        log.warn("SAGA [{}]: Payment FAILED. Compensating: unlocking funds.", state.getSagaId());

        saveOutboxEvent(state.getSagaId(), "contribution.command.unlock_funds", payload);
        saveOutboxEvent(state.getSagaId(), "claim.command.fail", payload);
        saveOutboxEvent(state.getSagaId(), "notification.command.send",
                "{\"status\": \"FAILED\", \"message\": \"Loan disbursement failed. Funds have been restored.\"}");
    }

    /**
     * Simple UUID extractor from JSON string.
     * In production, use Jackson ObjectMapper with proper DTOs.
     */
    private UUID extractUUID(String json, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf("\"", start);
            return UUID.fromString(json.substring(start, end));
        } catch (Exception e) {
            log.error("Failed to extract {} from payload: {}", key, json);
            return UUID.randomUUID(); // Fallback — would fail in production
        }
    }
}
