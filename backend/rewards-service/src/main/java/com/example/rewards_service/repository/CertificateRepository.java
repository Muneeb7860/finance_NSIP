package com.example.rewards_service.repository;

import com.example.rewards_service.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {
    List<Certificate> findByUserId(UUID userId);
}
