package com.example.payment_service.service;

import com.example.payment_service.model.Wallet;
import com.example.payment_service.model.WalletTransaction;
import com.example.payment_service.repository.WalletRepository;
import com.example.payment_service.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class WalletService {

    @Autowired private WalletRepository walletRepository;
    @Autowired private WalletTransactionRepository txRepository;

    @Transactional
    public Wallet createWallet(UUID userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Wallet already exists for this user.");
        }
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        return walletRepository.save(wallet);
    }

    public Wallet getWallet(UUID userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found."));
    }

    @Transactional
    public WalletTransaction topUp(UUID userId, BigDecimal amount, String referenceId) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Amount must be positive.");
        Wallet wallet = getWallet(userId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWalletId(wallet.getId()); tx.setUserId(userId);
        tx.setType(WalletTransaction.TransactionType.TOP_UP);
        tx.setAmount(amount); tx.setDescription("Wallet top-up via Stripe");
        tx.setReferenceId(referenceId);
        log.info("Wallet top-up: +{} for user {}. New balance: {}", amount, userId, wallet.getBalance());
        return txRepository.save(tx);
    }

    @Transactional
    public WalletTransaction debit(UUID userId, BigDecimal amount, WalletTransaction.TransactionType type, String desc) {
        Wallet wallet = getWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) throw new IllegalArgumentException("Insufficient wallet balance.");
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(wallet);

        WalletTransaction tx = new WalletTransaction();
        tx.setWalletId(wallet.getId()); tx.setUserId(userId);
        tx.setType(type); tx.setAmount(amount.negate()); tx.setDescription(desc);
        log.info("Wallet debit: -{} for user {}. Reason: {}. New balance: {}", amount, userId, desc, wallet.getBalance());
        return txRepository.save(tx);
    }

    public List<WalletTransaction> getTransactions(UUID userId) {
        Wallet wallet = getWallet(userId);
        return txRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }
}
