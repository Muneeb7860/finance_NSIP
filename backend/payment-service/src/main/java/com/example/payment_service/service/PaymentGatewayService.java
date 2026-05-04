package com.example.payment_service.service;

import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment Gateway Service with fault tolerance.
 *
 * FLAW #17 FIX:
 * - @Retry: Retries the gateway call up to 3 times with exponential backoff
 * - @CircuitBreaker: If the gateway fails repeatedly, the circuit opens and
 *   all requests are immediately rejected for 30 seconds (fail-fast),
 *   preventing cascading failures and allowing the gateway to recover.
 */
@Service
@Slf4j
public class PaymentGatewayService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Saga command: Disburse funds via payment gateway.
     */
    @KafkaListener(topics = "payment.command.disburse", groupId = "payment-group")
    @Transactional
    public void handleDisbursement(String payload) {
        log.info("Received disbursement command: {}", payload);

        Payment payment = new Payment();
        payment.setUserId(UUID.randomUUID()); // Would parse from payload
        payment.setAmount(new BigDecimal("15000"));
        payment.setMethod(Payment.PaymentMethod.STRIPE);
        payment.setStatus(Payment.PaymentStatus.PROCESSING);

        try {
            String txId = callGatewayWithResilience();

            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            payment.setGatewayTransactionId(txId);
            paymentRepository.save(payment);

            log.info("Payment SUCCESSFUL. Gateway TX: {}", txId);
            kafkaTemplate.send("payment.event.disbursed", payload);

        } catch (Exception e) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);

            log.error("Payment FAILED after retries: {}", e.getMessage());
            kafkaTemplate.send("payment.event.failed", payload);
        }
    }

    /**
     * Gateway call with Resilience4j annotations.
     *
     * @Retry: 3 attempts with 500ms, 1s, 2s exponential backoff
     * @CircuitBreaker: Opens after 5 failures in a 60-second window.
     *                  When open, calls fail immediately for 30 seconds.
     */
    @Retry(name = "paymentGateway", fallbackMethod = "gatewayFallback")
    @CircuitBreaker(name = "paymentGateway", fallbackMethod = "gatewayFallback")
    public String callGatewayWithResilience() {
        log.info("Attempting gateway call...");

        // Simulate calling Stripe / Paytm / PhonePe API
        boolean success = Math.random() > 0.05; // 95% success rate

        if (!success) {
            throw new RuntimeException("Gateway timeout — service unavailable");
        }

        return "ch_" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Fallback when both retry and circuit breaker are exhausted.
     * Returns a meaningful error instead of crashing.
     */
    public String gatewayFallback(Exception e) {
        log.warn("Circuit breaker activated. Gateway is unavailable: {}", e.getMessage());
        throw new RuntimeException("Payment gateway is currently unavailable. Please try again later.");
    }

    /**
     * Process a direct contribution payment from an employer.
     */
    @Transactional
    public Payment processContributionPayment(UUID userId, BigDecimal amount, Payment.PaymentMethod method) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setGatewayTransactionId("contrib_" + UUID.randomUUID().toString().substring(0, 8));

        Payment saved = paymentRepository.save(payment);
        log.info("Contribution payment processed: {} via {}", amount, method);
        return saved;
    }
}
