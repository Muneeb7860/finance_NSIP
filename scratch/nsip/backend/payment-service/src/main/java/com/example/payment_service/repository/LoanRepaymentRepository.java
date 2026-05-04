package com.example.payment_service.repository;

import com.example.payment_service.model.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, UUID> {
    Optional<LoanRepayment> findByClaimId(UUID claimId);
    List<LoanRepayment> findByUserId(UUID userId);
    List<LoanRepayment> findByNextDueDateBeforeAndStatus(LocalDate date, LoanRepayment.RepaymentStatus status);
}
