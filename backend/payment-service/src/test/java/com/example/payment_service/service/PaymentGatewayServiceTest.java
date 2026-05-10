package com.example.payment_service.service;

import com.example.payment_service.model.Payment;
import com.example.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class PaymentGatewayServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private PaymentGatewayService paymentGatewayService;

    @Test
    void testHandleDisbursement_Success() {
        String payload = "test-payload";
        
        // Use a spy to mock the resilience method or just let it run
        paymentGatewayService.handleDisbursement(payload);

        verify(paymentRepository).save(any(Payment.class));
        verify(kafkaTemplate).send(eq("payment.event.disbursed"), eq(payload));
    }

    @Test
    void testProcessContributionPayment_Success() {
        UUID userId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100");
        Payment.PaymentMethod method = Payment.PaymentMethod.STRIPE;

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);

        Payment result = paymentGatewayService.processContributionPayment(userId, amount, method);

        assertNotNull(result);
        assertEquals(amount, result.getAmount());
        assertEquals(Payment.PaymentStatus.COMPLETED, result.getStatus());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testGatewayFallback() {
        assertThrows(RuntimeException.class, () -> 
            paymentGatewayService.gatewayFallback(new RuntimeException("Test Error"))
        );
    }
}
