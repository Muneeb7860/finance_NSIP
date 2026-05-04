package com.example.contribution_service.controller;

import com.example.contribution_service.service.PensionEstimationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contributions/pension")
public class PensionController {

    @Autowired
    private PensionEstimationService pensionService;

    @GetMapping("/estimate")
    public ResponseEntity<?> estimatePension(
            @RequestParam UUID userId,
            @RequestParam int currentAge,
            @RequestParam BigDecimal monthlySalary) {
        return ResponseEntity.ok(pensionService.calculateEstimate(userId, currentAge, monthlySalary));
    }
}
