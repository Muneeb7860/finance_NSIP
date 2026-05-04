package com.example.payment_service.service;

import com.example.payment_service.application.port.out.PaymentGatewayPort;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentGatewayPort paymentGateway;

    public PaymentService(PaymentGatewayPort paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public boolean executePayment(String userId, BigDecimal amount) {
        // Business logic for payment execution
        return paymentGateway.processPayment(userId, amount, "SAR");
    }
}
