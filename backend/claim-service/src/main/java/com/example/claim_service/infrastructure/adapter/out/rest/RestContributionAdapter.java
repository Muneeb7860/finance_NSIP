package com.example.claim_service.infrastructure.adapter.out.rest;

import com.example.claim_service.application.port.out.ContributionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RestContributionAdapter implements ContributionPort {

    private final RestTemplate restTemplate;
    private static final String CONTRIBUTION_SERVICE_URL = "http://contribution-service:8084/api/v1/contributions/balance/";

    @Override
    public BigDecimal getTotalSavings(String userId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(CONTRIBUTION_SERVICE_URL + userId, Map.class);
            if (response != null && response.containsKey("balance")) {
                return new BigDecimal(response.get("balance").toString());
            }
        } catch (Exception e) {
            // Log and return 0 for safety in demo
        }
        return BigDecimal.ZERO;
    }
}
