package com.example.payment_service.infrastructure.adapter.out.payment;

import com.example.payment_service.application.port.out.PaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@Primary
public class StripeAdapter implements PaymentGatewayPort {
    private static final Logger log = LoggerFactory.getLogger(StripeAdapter.class);

    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency) {
        log.info("Processing $[{}] via STRIPE for user [{}]", amount, userId);
        // Stripe SDK logic here
        return true;
    }

    @Override
    public boolean refundPayment(String transactionId) {
        log.info("Refunding transaction [{}] via STRIPE", transactionId);
        return true;
    }
}
