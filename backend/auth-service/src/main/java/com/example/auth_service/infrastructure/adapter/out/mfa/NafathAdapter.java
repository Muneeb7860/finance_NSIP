package com.example.auth_service.infrastructure.adapter.out.mfa;

import com.example.auth_service.application.port.out.MFAProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NafathAdapter implements MFAProviderPort {
    @Override
    public boolean sendChallenge(String userId, String contactInfo) {
        log.info("Triggering Nafath App Push for user: {}", userId);
        // Integration with ELM / Nafath API would go here
        return true;
    }

    @Override
    public boolean verifyChallenge(String userId, String token) {
        log.info("Verifying Nafath token [{}] for user: {}", token, userId);
        return true;
    }

    @Override
    public MFAType getType() {
        return MFAType.NAFATH;
    }
}
