package com.example.claim_service.infrastructure.adapter.out.messaging;

import com.example.claim_service.application.port.out.ClaimEventPort;
import com.example.claim_service.domain.model.Claim;
import com.example.claim_service.model.OutboxEvent;
import com.example.claim_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaClaimAdapter implements ClaimEventPort {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void emitLoanRequested(Claim claim) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of(
                "claimId", claim.getId(),
                "userId", claim.getUserId(),
                "amount", claim.getAmount(),
                "type", claim.getType()
            ));
            
            log.info("Saving 'loan.requested' outbox event for claim: {}", claim.getId());
            OutboxEvent event = OutboxEvent.builder()
                .aggregateType("Claim")
                .aggregateId(claim.getId().toString())
                .type("loan.requested")
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .processed(false)
                .build();
                
            outboxEventRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to save outbox event for loan.requested", e);
        }
    }
}
