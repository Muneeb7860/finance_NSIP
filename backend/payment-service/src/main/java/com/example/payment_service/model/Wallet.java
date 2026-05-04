package com.example.payment_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Digital Wallet — stores the user's prepaid balance.
 * Supports top-up (Stripe/local wallets), auto-debit for contributions,
 * and loan repayment deductions.
 */
@Entity
@Table(name = "wallets")
@Data
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    private String linkedBankName;
    private String linkedBankAccountLast4;
    private boolean autoDebitEnabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
