package com.example.claim_service.domain.service;

import com.example.claim_service.application.port.in.ClaimUseCase;
import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.application.port.out.ClaimReviewerPort;
import com.example.claim_service.domain.model.Claim;
import java.time.LocalDateTime;
import java.util.List;

public class ClaimDomainService implements ClaimUseCase {

    private final ClaimRepositoryPort repository;
    private final ClaimReviewerPort reviewer;

    public ClaimDomainService(ClaimRepositoryPort repository, ClaimReviewerPort reviewer) {
        this.repository = repository;
        this.reviewer = reviewer;
    }

    @Override
    public Claim submitClaim(Claim claim) {
        // Business Rule: Validate 3-year vesting for loans
        if (claim.getType() == Claim.ClaimType.PERSONAL_LOAN) {
            validateVestingPeriod(claim.getUserId());
        }

        claim.setStatus(Claim.ClaimStatus.PENDING);
        claim.setCreatedAt(LocalDateTime.now());
        
        Claim saved = repository.save(claim);
        
        // Trigger AI Review
        try {
            String[] aiResults = reviewer.review(saved);
            saved.setAiRecommendation(aiResults[0]);
            saved.setAiReasoning(aiResults[1]);
            return repository.save(saved);
        } catch (Exception e) {
            // Log and continue — don't block the submission if AI fails
            return saved;
        }
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
