package com.example.saga_orchestrator.repository;

import com.example.saga_orchestrator.model.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SagaStateRepository extends JpaRepository<SagaState, UUID> {
    Optional<SagaState> findByClaimId(UUID claimId);
    List<SagaState> findByStatus(SagaState.SagaStatus status);
}
