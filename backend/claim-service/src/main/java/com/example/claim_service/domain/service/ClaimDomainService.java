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
    private final com.example.claim_service.application.port.out.ClaimEventPort eventPort;

    public ClaimDomainService(ClaimRepositoryPort repository, 
                              ClaimReviewerPort reviewer,
                              com.example.claim_service.application.port.out.ClaimEventPort eventPort) {
        this.repository = repository;
        this.reviewer = reviewer;
        this.eventPort = eventPort;
    }

    @Override
    public Claim submitClaim(Claim claim) {
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
            return saved;
        }
    }

    @Override
    public Claim submitLoanRequest(Claim claim) {
        // Business Rule: Validate 3-year vesting for loans
        validateVestingPeriod(claim.getUserId());

        claim.setStatus(Claim.ClaimStatus.PENDING);
        claim.setCreatedAt(LocalDateTime.now());
        
        Claim saved = repository.save(claim);
        
        // Emit event to start SAGA
        eventPort.emitLoanRequested(saved);
        
        return saved;
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
