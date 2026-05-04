package com.example.auth_service.application.port.out;

public interface MFAProviderPort {
    boolean sendChallenge(String userId, String contactInfo);
    boolean verifyChallenge(String userId, String codeOrToken);
    MFAType getType();

    enum MFAType {
        OTP, NAFATH, BIOMETRIC, IRIS, PASSCODE
    }
}
