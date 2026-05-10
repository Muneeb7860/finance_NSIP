package com.example.auth_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("null")
public class LiveKitServiceTest {

    private LiveKitService liveKitService;

    @BeforeEach
    void setUp() {
        liveKitService = new LiveKitService();
        ReflectionTestUtils.setField(liveKitService, "apiKey", "test-key");
        ReflectionTestUtils.setField(liveKitService, "apiSecret", "test-secret-key-must-be-long-enough-for-hs256-standard");
    }

    @Test
    void testCreateToken() {
        String token = liveKitService.createToken("room-1", "user-1");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
}
