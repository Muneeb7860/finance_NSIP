package com.example.claim_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Claim {
    private String id;
    private String userId;
    private ClaimType type;
    private BigDecimal amount;
    private String description;
    private ClaimStatus status;
    private LocalDateTime createdAt;

    public Claim() {}

    public Claim(String id, String userId, ClaimType type, BigDecimal amount, String description, ClaimStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public ClaimType getType() { return type; }
    public void setType(ClaimType type) { this.type = type; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum ClaimType {
        PERSONAL_LOAN, EMERGENCY_RELIEF, PENSION_WITHDRAWAL
    }

    public enum ClaimStatus {
        PENDING, APPROVED, REJECTED, SAGA_IN_PROGRESS
    }
}
