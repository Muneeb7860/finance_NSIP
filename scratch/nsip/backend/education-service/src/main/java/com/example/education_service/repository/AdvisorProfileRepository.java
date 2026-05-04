package com.example.education_service.repository;

import com.example.education_service.model.AdvisorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdvisorProfileRepository extends JpaRepository<AdvisorProfile, UUID> {
    Optional<AdvisorProfile> findByUserId(UUID userId);
    List<AdvisorProfile> findByActiveTrue();
    List<AdvisorProfile> findBySpecialtyAndActiveTrue(String specialty);
}
