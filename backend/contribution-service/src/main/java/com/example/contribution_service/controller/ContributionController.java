package com.example.contribution_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/contributions")
public class ContributionController {
    
    @GetMapping("/user/{userId}/vested")
    public ResponseEntity<Boolean> isUserVested(@PathVariable String userId) {
        // Mock implementation for Phase 2 validation
        // In a real scenario, this would check if user has 3+ years of contributions
        return ResponseEntity.ok(true);
    }
    
    @GetMapping("/user/{userId}/vested-balance")
    public ResponseEntity<BigDecimal> getVestedBalance(@PathVariable String userId) {
        // Mock implementation for Phase 2 validation
        return ResponseEntity.ok(new BigDecimal("150000.00"));
    }
    
    @GetMapping("/user/{userId}/years")
    public ResponseEntity<Integer> getContributionYears(@PathVariable String userId) {
        // Mock implementation for Phase 2 validation
        return ResponseEntity.ok(5);
    }
}
