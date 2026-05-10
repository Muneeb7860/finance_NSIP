package com.example.auth_service.infrastructure.adapter.out.mfa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MfaAdaptersTest {

    @Test
    void testOtpAdapter() {
        OtpAdapter adapter = new OtpAdapter();
        assertTrue(adapter.sendChallenge("user-1", "123456"));
        assertTrue(adapter.verifyChallenge("user-1", "654321"));
        assertNotNull(adapter.getType());
    }

    @Test
    void testBiometricAdapter() {
        BiometricAdapter adapter = new BiometricAdapter();
        assertTrue(adapter.sendChallenge("user-1", "face"));
        assertTrue(adapter.verifyChallenge("user-1", "valid"));
        assertNotNull(adapter.getType());
    }

    @Test
    void testNafathAdapter() {
        NafathAdapter adapter = new NafathAdapter();
        assertTrue(adapter.sendChallenge("user-1", "national-id"));
        assertTrue(adapter.verifyChallenge("user-1", "approved"));
        assertNotNull(adapter.getType());
    }
}
