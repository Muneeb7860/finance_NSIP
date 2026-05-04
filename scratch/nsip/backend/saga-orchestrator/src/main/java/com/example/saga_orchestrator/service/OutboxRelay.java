package com.example.saga_orchestrator.service;

import com.example.saga_orchestrator.model.OutboxEvent;
import com.example.saga_orchestrator.repository.OutboxEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class OutboxRelay {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByProcessedFalseOrderByCreatedAtAsc();
        
        for (OutboxEvent event : pendingEvents) {
            try {
                // Send to Kafka
                String topic = Objects.requireNonNull(event.getType(), "Event type (topic) cannot be null");
                String payload = Objects.requireNonNull(event.getPayload(), "Event payload cannot be null");
                kafkaTemplate.send(topic, payload);
                
                // Mark as processed (or delete) to prevent resending
                event.setProcessed(true);
                outboxEventRepository.save(event);
                
                log.info("OutboxRelay: Published event {} to topic {}", event.getId(), event.getType());
            } catch (Exception e) {
                log.error("OutboxRelay: Failed to publish event {}", event.getId(), e);
                // Will retry on next schedule
            }
        }
    }
}
