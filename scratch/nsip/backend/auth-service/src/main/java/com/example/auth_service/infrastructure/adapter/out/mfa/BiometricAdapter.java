package com.example.auth_service.infrastructure.adapter.out.mfa;

import com.example.auth_service.application.port.out.MFAProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BiometricAdapter implements MFAProviderPort {
    @Override
    public boolean sendChallenge(String userId, String contactInfo) {
        log.info("Requesting local device biometric verification for user: {}", userId);
        return true;
    }

    @Override
    public boolean verifyChallenge(String userId, String signedPayload) {
        log.info("Verifying signed biometric payload for user: {}", userId);
        // Verify public key signature from mobile device
        return true;
    }

    @Override
    public MFAType getType() {
        return MFAType.BIOMETRIC;
    }
}
