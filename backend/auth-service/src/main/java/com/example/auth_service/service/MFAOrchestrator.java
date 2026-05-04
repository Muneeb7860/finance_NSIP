package com.example.auth_service.service;

import com.example.auth_service.application.port.out.MFAProviderPort;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MFAOrchestrator {

    private final List<MFAProviderPort> mfaProviders;
    private final UserRepository userRepository;

    public Map<String, Object> initiateMfa(String nationalId) {
        User user = userRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isMfaEnabled()) {
            return Map.of("mfa_required", false, "status", "SUCCESS");
        }

        // Send challenge via preferred method
        MFAProviderPort provider = getProvider(user.getPreferredMfaType());
        provider.sendChallenge(user.getId().toString(), user.getEmail());

        return Map.of(
            "mfa_required", true,
            "method", user.getPreferredMfaType(),
            "status", "CHALLENGE_SENT"
        );
    }

    public boolean verifyMfa(String nationalId, String code) {
        User user = userRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        MFAProviderPort provider = getProvider(user.getPreferredMfaType());
        return provider.verifyChallenge(user.getId().toString(), code);
    }

    private MFAProviderPort getProvider(String type) {
        return mfaProviders.stream()
                .filter(p -> p.getType().name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unsupported MFA type: " + type));
    }
}
