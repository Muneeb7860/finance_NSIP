package com.example.payment_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Tracks loan repayment schedules.
 * When a loan is disbursed, a set of monthly repayment records is generated.
 * Each month, a scheduled job checks for due repayments and triggers auto-debit.
 */
@Entity
@Table(name = "loan_repayments")
@Data
public class LoanRepayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID claimId;

    @Column(nullable = false)
    private BigDecimal totalLoanAmount;

    @Column(nullable = false)
    private BigDecimal monthlyInstallment;

    @Column(nullable = false)
    private BigDecimal remainingBalance;

    @Column(nullable = false)
    private int totalMonths;

    @Column(nullable = false)
    private int monthsPaid;

    @Column(nullable = false)
    private LocalDate nextDueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepaymentStatus status = RepaymentStatus.ACTIVE;

    public enum RepaymentStatus {
        ACTIVE, COMPLETED, DEFAULTED
    }
}
