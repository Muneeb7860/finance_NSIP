package com.example.contribution_service.service;

import com.example.contribution_service.model.Contribution;
import com.example.contribution_service.model.Employment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the 4% salary contribution calculation.
 * This is the core business rule of the platform — every edge case must be covered.
 */
class ContributionCalculatorServiceTest {

    private ContributionCalculatorService calculatorService;
    private Employment employment;

    @BeforeEach
    void setUp() {
        calculatorService = new ContributionCalculatorService();
        employment = new Employment();
    }

    @Test
    @DisplayName("Standard salary: 4% of SAR 12,000 = SAR 480")
    void testStandardSalaryDeduction() {
        employment.setCurrentSalary(new BigDecimal("12000"));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 4));

        assertEquals(0, new BigDecimal("480.00").compareTo(result.getAmount()));
        assertEquals("Pending", result.getStatus());
        assertEquals("2026-04", result.getContributionMonth());
    }

    @Test
    @DisplayName("High salary: 4% of SAR 50,000 = SAR 2,000")
    void testHighSalaryDeduction() {
        employment.setCurrentSalary(new BigDecimal("50000"));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 5));

        assertEquals(0, new BigDecimal("2000.00").compareTo(result.getAmount()));
    }

    @Test
    @DisplayName("Minimum wage salary: 4% of SAR 4,000 = SAR 160")
    void testMinimumWageSalary() {
        employment.setCurrentSalary(new BigDecimal("4000"));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 1));

        assertEquals(0, new BigDecimal("160.00").compareTo(result.getAmount()));
    }

    @Test
    @DisplayName("Salary with decimals: 4% of SAR 7,500.50 = SAR 300.02")
    void testDecimalSalary() {
        employment.setCurrentSalary(new BigDecimal("7500.50"));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 3));

        assertEquals(0, new BigDecimal("300.02").compareTo(result.getAmount()));
    }

    @Test
    @DisplayName("Zero salary should return zero contribution")
    void testZeroSalary() {
        employment.setCurrentSalary(BigDecimal.ZERO);

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 6));

        assertEquals(0, result.getAmount().compareTo(BigDecimal.ZERO));
    }

    @ParameterizedTest
    @DisplayName("Parameterized: various salaries produce correct 4% deduction")
    @CsvSource({
            "10000, 400.00",
            "25000, 1000.00",
            "100000, 4000.00",
            "1, 0.04"
    })
    void testParameterizedSalaries(String salary, String expectedContribution) {
        employment.setCurrentSalary(new BigDecimal(salary));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 1));

        assertEquals(0, new BigDecimal(expectedContribution).compareTo(result.getAmount()));
    }

    @Test
    @DisplayName("Contribution should be linked to the correct employment record")
    void testContributionLinkedToEmployment() {
        employment.setCurrentSalary(new BigDecimal("15000"));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 2));

        assertSame(employment, result.getEmployment());
    }

    @Test
    @DisplayName("Month formatting: YearMonth serializes correctly")
    void testMonthFormatting() {
        employment.setCurrentSalary(new BigDecimal("10000"));

        Contribution result = calculatorService.calculateMonthlyContribution(employment, YearMonth.of(2026, 12));

        assertEquals("2026-12", result.getContributionMonth());
    }
}
