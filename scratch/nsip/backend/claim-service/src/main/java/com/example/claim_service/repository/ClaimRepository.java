package com.example.claim_service.repository;

import com.example.claim_service.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ClaimRepository extends JpaRepository<Claim, UUID> {
    List<Claim> findByUserId(UUID userId);
    List<Claim> findByStatus(Claim.ClaimStatus status);
    List<Claim> findByClaimType(Claim.ClaimType claimType);
}
