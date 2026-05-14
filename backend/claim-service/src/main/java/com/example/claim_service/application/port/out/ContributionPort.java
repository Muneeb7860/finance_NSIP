package com.example.claim_service.application.port.out;

import java.math.BigDecimal;

public interface ContributionPort {
    BigDecimal getTotalSavings(String userId);
}
