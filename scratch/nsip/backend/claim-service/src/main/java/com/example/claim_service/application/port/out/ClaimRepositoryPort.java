package com.example.claim_service.application.port.out;

import com.example.claim_service.domain.model.Claim;
import java.util.List;
import java.util.Optional;

public interface ClaimRepositoryPort {
    Claim save(Claim claim);
    Optional<Claim> findById(String id);
    List<Claim> findByUserId(String userId);
}
