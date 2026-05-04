package com.example.education_service.repository;

import com.example.education_service.model.ChronicCareEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChronicCareEnrollmentRepository extends JpaRepository<ChronicCareEnrollment, UUID> {
    List<ChronicCareEnrollment> findByUserId(UUID userId);
    boolean existsByUserIdAndProgramId(UUID userId, UUID programId);
}
