package com.example.claim_service.infrastructure.adapter.out.messaging;

import com.example.claim_service.application.port.out.ClaimEventPort;
import com.example.claim_service.domain.model.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaClaimAdapter implements ClaimEventPort {

    private final KafkaTemplate<String, String> kafkaTemplate;
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
            
            log.info("EMITTING EVENT 'loan.requested' for claim: {}", claim.getId());
            kafkaTemplate.send("loan.requested", payload);
        } catch (Exception e) {
            log.error("Failed to emit loan.requested event", e);
        }
    }
}
