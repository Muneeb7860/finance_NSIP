package com.example.claim_service.infrastructure.adapter.out.messaging;

import com.example.claim_service.model.OutboxEvent;
import com.example.claim_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.processor.delay:5000}")
    @Transactional
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();
        if (events.isEmpty()) {
            return;
        }
        
        log.info("Processing {} outbox events", events.size());
        
        for (OutboxEvent event : events) {
            try {
                kafkaTemplate.send(event.getType(), event.getPayload()).get(); // Synchronous send to ensure delivery
                event.setProcessed(true);
                outboxEventRepository.save(event);
                log.debug("Successfully published event {} of type {}", event.getId(), event.getType());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}", event.getId(), e);
                // Stop processing on first failure to maintain strict ordering
                break;
            }
        }
    }
}
