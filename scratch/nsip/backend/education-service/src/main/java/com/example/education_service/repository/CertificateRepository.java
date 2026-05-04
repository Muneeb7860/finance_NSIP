package com.example.education_service.repository;

import com.example.education_service.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    List<Certificate> findByUserId(UUID userId);
    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);
}
