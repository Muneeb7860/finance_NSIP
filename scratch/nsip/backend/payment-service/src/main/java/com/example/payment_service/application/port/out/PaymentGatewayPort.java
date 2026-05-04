package com.example.payment_service.application.port.out;

import java.math.BigDecimal;

public interface PaymentGatewayPort {
    boolean processPayment(String userId, BigDecimal amount, String currency);
    boolean refundPayment(String transactionId);
}
