package com.example.claim_service.domain.service;

import com.example.claim_service.application.port.in.ClaimUseCase;
import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.domain.model.Claim;
import java.time.LocalDateTime;
import java.util.List;

public class ClaimDomainService implements ClaimUseCase {

    private final ClaimRepositoryPort repository;

    public ClaimDomainService(ClaimRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Claim submitClaim(Claim claim) {
        // Business Rule: Validate 3-year vesting for loans
        if (claim.getType() == Claim.ClaimType.PERSONAL_LOAN) {
            validateVestingPeriod(claim.getUserId());
        }

        claim.setStatus(Claim.ClaimStatus.PENDING);
        claim.setCreatedAt(LocalDateTime.now());
        return repository.save(claim);
    }

    @Override
    public List<Claim> getClaimsByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Claim updateClaimStatus(String claimId, Claim.ClaimStatus status) {
        Claim claim = repository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));
        claim.setStatus(status);
        return repository.save(claim);
    }

    private void validateVestingPeriod(String userId) {
        // Logic to check contribution service (mocked for now)
        // In a real hexagonal system, this would call another OUT port (ContributionPort)
        boolean isVested = true; 
        if (!isVested) {
            throw new RuntimeException("User does not meet the 3-year vesting requirement.");
        }
    }
}
