package com.example.payment_service.repository;

import com.example.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByUserId(UUID userId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
}
