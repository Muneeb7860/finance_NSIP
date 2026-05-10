package com.example.payment_service.service;

import com.example.payment_service.application.port.out.PaymentGatewayPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock private PaymentGatewayPort paymentGateway;
    @InjectMocks private PaymentService paymentService;

    @Test
    void testExecutePayment() {
        when(paymentGateway.processPayment(anyString(), any(), anyString())).thenReturn(true);
        boolean result = paymentService.executePayment("user-1", new BigDecimal("100"));
        assertTrue(result);
        verify(paymentGateway).processPayment("user-1", new BigDecimal("100"), "SAR");
    }
}
