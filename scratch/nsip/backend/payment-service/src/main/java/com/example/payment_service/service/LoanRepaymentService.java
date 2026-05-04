package com.example.payment_service.service;

import com.example.payment_service.model.LoanRepayment;
import com.example.payment_service.repository.LoanRepaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Manages loan repayment lifecycle:
 * 1. Creates repayment schedule when a loan is disbursed
 * 2. Runs a monthly scheduled job to auto-debit installments
 * 3. Handles early payoff and default scenarios
 */
@Service
@Slf4j
public class LoanRepaymentService {

    @Autowired
    private LoanRepaymentRepository repaymentRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final int DEFAULT_REPAYMENT_MONTHS = 24; // 2-year repayment term

    /**
     * Create a repayment schedule when a loan is successfully disbursed.
     * Called after the Saga completes successfully.
     */
    @Transactional
    public LoanRepayment createRepaymentSchedule(UUID userId, UUID claimId, BigDecimal loanAmount) {
        BigDecimal monthlyInstallment = loanAmount.divide(
                new BigDecimal(DEFAULT_REPAYMENT_MONTHS), 2, RoundingMode.CEILING);

        LoanRepayment repayment = new LoanRepayment();
        repayment.setUserId(userId);
        repayment.setClaimId(claimId);
        repayment.setTotalLoanAmount(loanAmount);
        repayment.setMonthlyInstallment(monthlyInstallment);
        repayment.setRemainingBalance(loanAmount);
        repayment.setTotalMonths(DEFAULT_REPAYMENT_MONTHS);
        repayment.setMonthsPaid(0);
        repayment.setNextDueDate(LocalDate.now().plusMonths(1));
        repayment.setStatus(LoanRepayment.RepaymentStatus.ACTIVE);

        LoanRepayment saved = repaymentRepository.save(repayment);
        log.info("Repayment schedule created: {} monthly x {} months for claim {}",
                monthlyInstallment, DEFAULT_REPAYMENT_MONTHS, claimId);

        return saved;
    }

    /**
     * Scheduled job: Runs on the 1st of every month at 6:00 AM.
     * Finds all active loans with a due date in the past and processes auto-debit.
     */
    @Scheduled(cron = "0 0 6 1 * ?") // 1st of every month at 06:00
    @Transactional
    public void processMonthlyRepayments() {
        List<LoanRepayment> dueLoans = repaymentRepository
                .findByNextDueDateBeforeAndStatus(LocalDate.now(), LoanRepayment.RepaymentStatus.ACTIVE);

        for (LoanRepayment loan : dueLoans) {
            processInstallment(loan);
        }

        log.info("Monthly repayment batch: processed {} loans", dueLoans.size());
    }

    /**
     * Process a single installment payment.
     */
    @Transactional
    public void processInstallment(LoanRepayment loan) {
        BigDecimal newBalance = loan.getRemainingBalance().subtract(loan.getMonthlyInstallment());

        if (newBalance.compareTo(BigDecimal.ZERO) <= 0) {
            // Loan fully paid off
            loan.setRemainingBalance(BigDecimal.ZERO);
            loan.setMonthsPaid(loan.getMonthsPaid() + 1);
            loan.setStatus(LoanRepayment.RepaymentStatus.COMPLETED);
            repaymentRepository.save(loan);

            log.info("Loan COMPLETED for user {} (claim {})", loan.getUserId(), loan.getClaimId());
            kafkaTemplate.send("notification.command.send",
                    String.format("{\"userId\":\"%s\", \"status\":\"SUCCESS\", \"message\":\"Your loan has been fully repaid! Congratulations!\"}", loan.getUserId()));
        } else {
            loan.setRemainingBalance(newBalance);
            loan.setMonthsPaid(loan.getMonthsPaid() + 1);
            loan.setNextDueDate(loan.getNextDueDate().plusMonths(1));
            repaymentRepository.save(loan);

            log.info("Installment processed for user {}. Remaining: {}. Next due: {}",
                    loan.getUserId(), newBalance, loan.getNextDueDate());
            kafkaTemplate.send("notification.command.send",
                    String.format("{\"userId\":\"%s\", \"status\":\"INFO\", \"message\":\"Monthly installment of SAR %s deducted. Remaining balance: SAR %s\"}",
                            loan.getUserId(), loan.getMonthlyInstallment(), newBalance));
        }
    }

    /**
     * Get repayment details for a user.
     */
    public List<LoanRepayment> getUserRepayments(UUID userId) {
        return repaymentRepository.findByUserId(userId);
    }
}
