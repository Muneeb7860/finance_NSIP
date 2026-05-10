package com.example.payment_service.service;

import com.example.payment_service.model.Wallet;
import com.example.payment_service.model.WalletTransaction;
import com.example.payment_service.repository.WalletRepository;
import com.example.payment_service.repository.WalletTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class WalletServiceTest {

    @Mock private WalletRepository walletRepository;
    @Mock private WalletTransactionRepository txRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    void testCreateWallet_Success() {
        UUID userId = UUID.randomUUID();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenAnswer(i -> i.getArguments()[0]);

        Wallet result = walletService.createWallet(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void testTopUp_Success() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("100"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(txRepository.save(any(WalletTransaction.class))).thenAnswer(i -> i.getArguments()[0]);

        WalletTransaction result = walletService.topUp(userId, new BigDecimal("50"), "ref-1");

        assertNotNull(result);
        assertEquals(new BigDecimal("50"), result.getAmount());
        assertEquals(new BigDecimal("150"), wallet.getBalance());
        verify(walletRepository).save(wallet);
    }

    @Test
    void testDebit_Success() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(new BigDecimal("100"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));
        when(txRepository.save(any(WalletTransaction.class))).thenAnswer(i -> i.getArguments()[0]);

        WalletTransaction result = walletService.debit(userId, new BigDecimal("40"), WalletTransaction.TransactionType.LOAN_REPAYMENT, "debit");

        assertNotNull(result);
        assertEquals(new BigDecimal("-40"), result.getAmount());
        assertEquals(new BigDecimal("60"), wallet.getBalance());
    }

    @Test
    void testDebit_InsufficientBalance() {
        UUID userId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("20"));

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        assertThrows(IllegalArgumentException.class, () -> 
            walletService.debit(userId, new BigDecimal("50"), WalletTransaction.TransactionType.LOAN_REPAYMENT, "fail")
        );
    }
}
