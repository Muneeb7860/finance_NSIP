package com.example.education_service.repository;

import com.example.education_service.model.AdvisorSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AdvisorSessionRepository extends JpaRepository<AdvisorSession, UUID> {
    List<AdvisorSession> findByAdvisorIdOrderByScheduledAtDesc(UUID advisorId);
    List<AdvisorSession> findByCustomerIdOrderByBookedAtDesc(UUID customerId);
}
