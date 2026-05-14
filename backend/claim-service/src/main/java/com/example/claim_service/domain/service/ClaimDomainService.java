package com.example.claim_service.domain.service;

import com.example.claim_service.application.port.in.ClaimUseCase;
import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.application.port.out.ClaimReviewerPort;
import com.example.claim_service.domain.model.Claim;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

import com.example.common.constants.CommonConstants;

public class ClaimDomainService implements ClaimUseCase {

    private final ClaimRepositoryPort repository;
    private final ClaimReviewerPort reviewer;
    private final com.example.claim_service.application.port.out.ClaimEventPort eventPort;
    private final com.example.claim_service.application.port.out.ContributionPort contributionPort;

    public ClaimDomainService(ClaimRepositoryPort repository, 
                              ClaimReviewerPort reviewer,
                              com.example.claim_service.application.port.out.ClaimEventPort eventPort,
                              com.example.claim_service.application.port.out.ContributionPort contributionPort) {
        this.repository = repository;
        this.reviewer = reviewer;
        this.eventPort = eventPort;
        this.contributionPort = contributionPort;
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

        // Business Rule: Loan capped at 30% of vested savings
        BigDecimal savings = contributionPort.getTotalSavings(claim.getUserId());
        BigDecimal cap30 = savings.multiply(CommonConstants.LOAN_CAP_PERCENT);
        
        // Business Rule: Hard max cap of SAR 45,000 (BRD 4.2)
        BigDecimal finalCap = cap30.min(CommonConstants.LOAN_HARD_MAX_SAR);
        
        if (claim.getAmount().compareTo(finalCap) > 0) {
            String message = "Loan amount exceeds limits.";
            if (cap30.compareTo(CommonConstants.LOAN_HARD_MAX_SAR) > 0) {
                message += " The national hard cap is SAR " + CommonConstants.LOAN_HARD_MAX_SAR.toPlainString();
            } else {
                message += " Your personal eligibility (30% of savings) is SAR " + cap30.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
            }
            throw new RuntimeException(message);
        }

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
