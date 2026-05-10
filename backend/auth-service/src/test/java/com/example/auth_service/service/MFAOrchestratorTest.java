package com.example.auth_service.service;

import com.example.auth_service.application.port.out.MFAProviderPort;
import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class MFAOrchestratorTest {

    @Mock private UserRepository userRepository;
    @Mock private MFAProviderPort provider;

    @InjectMocks
    private MFAOrchestrator mfaOrchestrator;

    @Test
    void testInitiateMfa_Required() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setMfaEnabled(true);
        user.setPreferredMfaType("OTP");
        user.setEmail("test@test.com");

        when(userRepository.findByNationalId("123")).thenReturn(Optional.of(user));
        
        when(provider.getType()).thenReturn(MFAProviderPort.MFAType.OTP);
        
        MFAOrchestrator orchestrator = new MFAOrchestrator(List.of(provider), userRepository);

        Map<String, Object> result = orchestrator.initiateMfa("123");

        assertTrue((Boolean) result.get("mfa_required"));
        verify(provider).sendChallenge(any(), any());
    }

    @Test
    void testInitiateMfa_NotRequired() {
        User user = new User();
        user.setMfaEnabled(false);
        when(userRepository.findByNationalId("123")).thenReturn(Optional.of(user));

        Map<String, Object> result = mfaOrchestrator.initiateMfa("123");

        assertFalse((Boolean) result.get("mfa_required"));
    }
}
