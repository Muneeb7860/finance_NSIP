package com.example.contribution_service.service;

import com.example.contribution_service.model.Contribution;
import com.example.contribution_service.model.Employment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;

@Service
public class ContributionCalculatorService {

    // Business Requirement: 4% of every salary every month
    private static final BigDecimal CONTRIBUTION_RATE = new BigDecimal("0.04");

    public Contribution calculateMonthlyContribution(Employment employment, YearMonth month) {
        if (employment == null || employment.getCurrentSalary() == null) {
            throw new IllegalArgumentException("Employment details and salary cannot be null.");
        }
        
        Contribution contribution = new Contribution();
        contribution.setEmployment(employment);
        contribution.setContributionMonth(month.toString());
        
        // Calculate 4% of the salary and explicitly round to 2 decimal places (standard for currency)
        BigDecimal contributionAmount = employment.getCurrentSalary()
                .multiply(CONTRIBUTION_RATE)
                .setScale(2, java.math.RoundingMode.HALF_UP);
                
        contribution.setAmount(contributionAmount);
        contribution.setStatus("Pending");
        
        return contribution;
    }
}
