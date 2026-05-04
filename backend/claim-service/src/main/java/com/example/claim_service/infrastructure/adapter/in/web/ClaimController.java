package com.example.claim_service.infrastructure.adapter.in.web;

import com.example.claim_service.application.port.in.ClaimUseCase;
import com.example.claim_service.domain.model.Claim;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v2/claims") // Versioning for Hexagonal transition
public class ClaimController {

    private final ClaimUseCase claimUseCase;

    public ClaimController(ClaimUseCase claimUseCase) {
        this.claimUseCase = claimUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Claim> submitClaim(@RequestBody Map<String, String> body) {
        Claim claim = new Claim();
        claim.setUserId(body.get("userId"));
        claim.setType(Claim.ClaimType.valueOf(body.get("claimType")));
        claim.setAmount(new BigDecimal(body.get("amount")));
        claim.setDescription(body.get("description"));
        
        return ResponseEntity.ok(claimUseCase.submitClaim(claim));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Claim>> getUserClaims(@PathVariable String userId) {
        return ResponseEntity.ok(claimUseCase.getClaimsByUserId(userId));
    }

    @PatchMapping("/{claimId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Claim> updateStatus(@PathVariable String claimId, @RequestParam Claim.ClaimStatus status) {
        return ResponseEntity.ok(claimUseCase.updateClaimStatus(claimId, status));
    }
}
