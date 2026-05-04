package com.example.contribution_service.service;

import com.example.contribution_service.model.FundLock;
import com.example.contribution_service.repository.ContributionRepository;
import com.example.contribution_service.repository.FundLockRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * FLAW #7 FIX: Fund lock now uses real database queries instead of hardcoded values.
 * - Vesting date comes from the earliest contribution record
 * - Total savings are aggregated from the contributions table
 * - Locked amounts are tracked in the fund_locks table
 */
@Service
@Slf4j
public class FundLockService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ContributionRepository contributionRepository;

    @Autowired
    private FundLockRepository fundLockRepository;

    private static final BigDecimal PERSONAL_RATIO = new BigDecimal("0.30");
    private static final BigDecimal EMERGENCY_RATIO = new BigDecimal("0.70");
    private static final int VESTING_YEARS = 3;

    @KafkaListener(topics = "contribution.command.lock_funds", groupId = "contribution-group")
    @Transactional
    public void handleLockFunds(String payload) {
        log.info("Received lock_funds command: {}", payload);

        // Parse from payload (simplified — use Jackson in production)
        UUID userId = extractUUID(payload, "userId");
        UUID claimId = extractUUID(payload, "claimId");
        BigDecimal requestedAmount = extractAmount(payload);
        String loanType = extractString(payload, "type");

        // STEP 1: Vesting check — query the REAL first contribution date from DB
        java.util.Optional<java.time.LocalDateTime> firstContribution = contributionRepository.getFirstContributionDate(userId);
        if (firstContribution.isEmpty()) {
            log.warn("VESTING FAILED: No contributions found for user {}", userId);
            kafkaTemplate.send("contribution.event.funds_lock_failed", payload);
            return;
        }

        boolean isVested = firstContribution.get().toLocalDate().plusYears(VESTING_YEARS).isBefore(LocalDate.now());
        if (!isVested) {
            log.warn("VESTING FAILED: User {} first contributed on {}. 3-year lock not met.", userId, firstContribution.get());
            kafkaTemplate.send("contribution.event.funds_lock_failed", payload);
            return;
        }

        // STEP 2: Calculate REAL available balance from DB
        BigDecimal totalSavings = contributionRepository.getTotalSavingsByUserId(userId);
        BigDecimal alreadyLocked = fundLockRepository.getTotalLockedByUserId(userId);
        BigDecimal availableBalance = totalSavings.subtract(alreadyLocked);

        // STEP 3: Validate against the correct credit limit
        BigDecimal creditLimit;
        if ("PERSONAL_LOAN".equals(loanType)) {
            creditLimit = totalSavings.multiply(PERSONAL_RATIO);
        } else {
            creditLimit = totalSavings.multiply(EMERGENCY_RATIO);
        }

        if (requestedAmount.compareTo(creditLimit) > 0) {
            log.warn("CREDIT LIMIT EXCEEDED: Requested {} but limit is {}", requestedAmount, creditLimit);
            kafkaTemplate.send("contribution.event.funds_lock_failed", payload);
            return;
        }

        if (requestedAmount.compareTo(availableBalance) > 0) {
            log.warn("INSUFFICIENT AVAILABLE: Requested {} but only {} available (after existing locks)", requestedAmount, availableBalance);
            kafkaTemplate.send("contribution.event.funds_lock_failed", payload);
            return;
        }

        // STEP 4: Create a REAL fund lock record in the database
        FundLock lock = new FundLock();
        lock.setUserId(userId);
        lock.setClaimId(claimId);
        lock.setLockedAmount(requestedAmount);
        lock.setStatus(FundLock.LockStatus.LOCKED);
        fundLockRepository.save(lock);

        log.info("Funds LOCKED: {} for user {} (claim {}). Total savings: {}, Now locked: {}",
                requestedAmount, userId, claimId, totalSavings, alreadyLocked.add(requestedAmount));

        kafkaTemplate.send("contribution.event.funds_locked", payload);
    }

    /**
     * Compensating transaction: Release a fund lock when payment fails.
     */
    @KafkaListener(topics = "contribution.command.unlock_funds", groupId = "contribution-group")
    @Transactional
    public void handleUnlockFunds(String payload) {
        UUID claimId = extractUUID(payload, "claimId");

        Optional<FundLock> lockOpt = fundLockRepository.findByClaimId(claimId);
        if (lockOpt.isPresent()) {
            FundLock lock = lockOpt.get();
            lock.setStatus(FundLock.LockStatus.RELEASED);
            fundLockRepository.save(lock);
            log.info("COMPENSATING: Fund lock RELEASED for claim {}. Amount: {}", claimId, lock.getLockedAmount());
        } else {
            log.warn("COMPENSATING: No fund lock found for claim {} — nothing to release.", claimId);
        }
    }

    // --- Simple payload parsers (replace with Jackson in production) ---

    private UUID extractUUID(String json, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf("\"", start);
            return UUID.fromString(json.substring(start, end));
        } catch (Exception e) {
            return UUID.randomUUID();
        }
    }

    private BigDecimal extractAmount(String json) {
        try {
            String search = "\"amount\":";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            return new BigDecimal(json.substring(start, end).trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String extractString(String json, String key) {
        try {
            String search = "\"" + key + "\":\"";
            int start = json.indexOf(search) + search.length();
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        } catch (Exception e) {
            return "";
        }
    }
}
