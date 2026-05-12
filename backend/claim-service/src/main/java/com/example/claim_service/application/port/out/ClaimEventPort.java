package com.example.claim_service.application.port.out;

import com.example.claim_service.domain.model.Claim;

public interface ClaimEventPort {
    void emitLoanRequested(Claim claim);
}
