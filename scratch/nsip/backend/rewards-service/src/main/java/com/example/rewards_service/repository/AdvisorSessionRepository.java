package com.example.rewards_service.repository;

import com.example.rewards_service.model.AdvisorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AdvisorSessionRepository extends JpaRepository<AdvisorSession, UUID> {
    List<AdvisorSession> findByUserId(UUID userId);
    List<AdvisorSession> findByAdvisorId(UUID advisorId);
}
