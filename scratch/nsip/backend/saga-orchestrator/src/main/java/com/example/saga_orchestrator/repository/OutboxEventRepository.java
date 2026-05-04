package com.example.saga_orchestrator.repository;

import com.example.saga_orchestrator.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {
    List<OutboxEvent> findByProcessedFalseOrderByCreatedAtAsc();
}
