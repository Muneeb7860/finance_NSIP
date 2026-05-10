package com.example.payment_service.service;

import com.example.payment_service.model.LoanRepayment;
import com.example.payment_service.repository.LoanRepaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class LoanRepaymentServiceTest {

    @Mock private LoanRepaymentRepository repaymentRepository;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private LoanRepaymentService loanRepaymentService;

    @Test
    void testCreateRepaymentSchedule() {
        UUID userId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("2400");
        
        when(repaymentRepository.save(any(LoanRepayment.class))).thenAnswer(i -> i.getArguments()[0]);

        LoanRepayment result = loanRepaymentService.createRepaymentSchedule(userId, claimId, amount);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getMonthlyInstallment());
        assertEquals(LoanRepayment.RepaymentStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testProcessMonthlyRepayments() {
        LoanRepayment loan = new LoanRepayment();
        loan.setUserId(UUID.randomUUID());
        loan.setRemainingBalance(new BigDecimal("1000"));
        loan.setMonthlyInstallment(new BigDecimal("100"));
        loan.setNextDueDate(LocalDate.now().plusMonths(1));

        when(repaymentRepository.findByNextDueDateBeforeAndStatus(any(), any())).thenReturn(List.of(loan));

        loanRepaymentService.processMonthlyRepayments();

        verify(repaymentRepository, atLeastOnce()).save(loan);
        verify(kafkaTemplate, atLeastOnce()).send(anyString(), anyString());
    }

    @Test
    void testProcessInstallment_FullPayoff() {
        LoanRepayment loan = new LoanRepayment();
        loan.setUserId(UUID.randomUUID());
        loan.setRemainingBalance(new BigDecimal("100"));
        loan.setMonthlyInstallment(new BigDecimal("100"));
        loan.setMonthsPaid(23);

        loanRepaymentService.processInstallment(loan);

        assertEquals(BigDecimal.ZERO, loan.getRemainingBalance());
        assertEquals(LoanRepayment.RepaymentStatus.COMPLETED, loan.getStatus());
        verify(repaymentRepository).save(loan);
    }
}
