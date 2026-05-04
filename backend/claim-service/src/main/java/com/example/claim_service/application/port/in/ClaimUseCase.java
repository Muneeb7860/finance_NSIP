package com.example.claim_service.application.port.in;

import com.example.claim_service.domain.model.Claim;
import java.util.List;

public interface ClaimUseCase {
    Claim submitClaim(Claim claim);
    List<Claim> getClaimsByUserId(String userId);
    Claim updateClaimStatus(String claimId, Claim.ClaimStatus status);
}
