package com.example.payment_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Wallet transaction ledger — every credit/debit is recorded immutably.
 */
@Entity
@Table(name = "wallet_transactions")
@Data
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private BigDecimal amount;

    private String description;
    private String referenceId; // Stripe charge ID, loan claim ID, etc.
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum TransactionType {
        TOP_UP, CONTRIBUTION_DEBIT, LOAN_REPAYMENT, CASHBACK, WITHDRAWAL
    }
}
