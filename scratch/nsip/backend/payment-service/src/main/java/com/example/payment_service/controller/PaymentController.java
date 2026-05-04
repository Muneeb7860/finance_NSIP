package com.example.payment_service.controller;

import com.example.payment_service.model.Payment;
import com.example.payment_service.service.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @PostMapping("/contribution")
    public ResponseEntity<?> processContribution(@RequestBody Map<String, String> body) {
        Payment payment = paymentGatewayService.processContributionPayment(
                UUID.fromString(body.get("userId")),
                new BigDecimal(body.get("amount")),
                Payment.PaymentMethod.valueOf(body.getOrDefault("method", "STRIPE"))
        );
        return ResponseEntity.ok(payment);
    }
}
