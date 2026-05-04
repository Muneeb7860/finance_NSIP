package com.example.auth_service.infrastructure.adapter.out.mfa;

import com.example.auth_service.application.port.out.MFAProviderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Random;

@Component
@Slf4j
public class OtpAdapter implements MFAProviderPort {
    @Override
    public boolean sendChallenge(String userId, String phoneNumber) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        log.info("SMS OTP [{}] sent to phone: {}", code, phoneNumber);
        // SMS Gateway integration (e.g. Twilio, Unifonic)
        return true;
    }

    @Override
    public boolean verifyChallenge(String userId, String code) {
        log.info("Verifying OTP code [{}] for user: {}", code, userId);
        return true;
    }

    @Override
    public MFAType getType() {
        return MFAType.OTP;
    }
}
