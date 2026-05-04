package com.example.payment_service.infrastructure.adapter.out.payment;

import com.example.payment_service.application.port.out.PaymentGatewayPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class MadaAdapter implements PaymentGatewayPort {
    private static final Logger log = LoggerFactory.getLogger(MadaAdapter.class);

    @Override
    public boolean processPayment(String userId, BigDecimal amount, String currency) {
        log.info("Processing SAR [{}] via MADA for user [{}]", amount, userId);
        // Mada specific logic here
        return true;
    }

    @Override
    public boolean refundPayment(String transactionId) {
        log.info("Refunding transaction [{}] via MADA", transactionId);
        return true;
    }
}
