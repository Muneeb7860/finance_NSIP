package com.example.claim_service.infrastructure.config;

import com.example.claim_service.application.port.out.ClaimRepositoryPort;
import com.example.claim_service.application.port.out.ClaimReviewerPort;
import com.example.claim_service.domain.service.ClaimDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClaimConfiguration {

    @Bean
    public ClaimDomainService claimDomainService(ClaimRepositoryPort repositoryPort, 
                                               ClaimReviewerPort reviewerPort,
                                               com.example.claim_service.application.port.out.ClaimEventPort eventPort) {
        return new ClaimDomainService(repositoryPort, reviewerPort, eventPort);
    }
}
