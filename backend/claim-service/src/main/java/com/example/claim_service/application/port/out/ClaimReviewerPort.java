package com.example.claim_service.application.port.out;

import com.example.claim_service.domain.model.Claim;

public interface ClaimReviewerPort {
    String[] review(Claim claim);
}
