package com.example.contribution_service.controller;

import com.example.contribution_service.model.Contribution;
import com.example.contribution_service.model.Employment;
import com.example.contribution_service.service.ContributionCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payroll")
public class PayrollController {

    @Autowired
    private ContributionCalculatorService calculatorService;

    // A mock endpoint to verify the 4% business rule
    @PostMapping("/calculate")
    public ResponseEntity<Contribution> calculateContribution(@RequestParam BigDecimal salary) {
        Employment mockEmployment = new Employment();
        mockEmployment.setId(UUID.randomUUID());
        mockEmployment.setCurrentSalary(salary);
        
        Contribution contribution = calculatorService.calculateMonthlyContribution(mockEmployment, YearMonth.now());
        
        return ResponseEntity.ok(contribution);
    }
}
