package com.example.saga_orchestrator.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Typed event object for loan request payloads.
 * Replaces fragile String.format() JSON construction with type-safe Java objects.
 * All services producing or consuming loan events should use this class.
 */
public class LoanRequestedEvent implements Serializable {
    private UUID claimId;
    private UUID userId;
    private BigDecimal amount;
    private String type; // PERSONAL_LOAN or EMERGENCY_RELIEF

    public LoanRequestedEvent() {}

    public LoanRequestedEvent(UUID claimId, UUID userId, BigDecimal amount, String type) {
        this.claimId = claimId;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
    }

    public UUID getClaimId() { return claimId; }
    public void setClaimId(UUID claimId) { this.claimId = claimId; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
