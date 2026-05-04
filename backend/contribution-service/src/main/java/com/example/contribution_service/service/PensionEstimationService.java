package com.example.contribution_service.service;

import com.example.contribution_service.repository.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * FLAW #14 FIX: Calculates estimated pension based on actual contribution data.
 *
 * Formula: Estimated Monthly Pension = (Total Savings * Annual Growth Rate * Years to Retirement) / (Retirement Duration in Months)
 *
 * Assumptions:
 * - Default retirement age: 60
 * - Conservative annual growth rate: 5%
 * - Expected retirement duration: 20 years (240 months)
 */
@Service
@Slf4j
public class PensionEstimationService {

    @Autowired
    private ContributionRepository contributionRepository;

    private static final BigDecimal ANNUAL_GROWTH_RATE = new BigDecimal("0.05");
    private static final int RETIREMENT_AGE = 60;
    private static final int RETIREMENT_DURATION_MONTHS = 240; // 20 years

    /**
     * Estimate monthly pension based on current savings, age, and projected contributions.
     */
    public PensionEstimate calculateEstimate(UUID userId, int currentAge, BigDecimal monthlySalary) {
        BigDecimal totalSavings = contributionRepository.getTotalSavingsByUserId(userId);
        int yearsToRetirement = Math.max(0, RETIREMENT_AGE - currentAge);

        // Project future contributions (4% of salary × 12 months × years to retirement)
        BigDecimal futureContributions = monthlySalary
                .multiply(new BigDecimal("0.04"))
                .multiply(new BigDecimal("12"))
                .multiply(new BigDecimal(yearsToRetirement));

        // Apply compound growth: FV = PV × (1 + r)^n
        BigDecimal growthMultiplier = BigDecimal.ONE.add(ANNUAL_GROWTH_RATE)
                .pow(yearsToRetirement);

        BigDecimal projectedCorpus = totalSavings.add(futureContributions)
                .multiply(growthMultiplier)
                .setScale(2, RoundingMode.HALF_UP);

        // Monthly pension = Corpus / Retirement duration
        BigDecimal monthlyPension = projectedCorpus
                .divide(new BigDecimal(RETIREMENT_DURATION_MONTHS), 2, RoundingMode.HALF_UP);

        return new PensionEstimate(
                totalSavings,
                futureContributions,
                projectedCorpus,
                monthlyPension,
                yearsToRetirement
        );
    }

    /**
     * Pension estimate response object.
     */
    public record PensionEstimate(
            BigDecimal currentSavings,
            BigDecimal projectedFutureContributions,
            BigDecimal projectedCorpusAtRetirement,
            BigDecimal estimatedMonthlyPension,
            int yearsToRetirement
    ) {}
}
