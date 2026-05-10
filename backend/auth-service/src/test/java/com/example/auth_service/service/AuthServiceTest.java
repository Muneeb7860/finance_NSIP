package com.example.auth_service.service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String secret = "nsip-platform-secret-key-must-be-at-least-256-bits-long-for-hs256";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtSecret", secret);
    }

    @Test
    void testRegister_Success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setRole(User.Role.CUSTOMER);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNationalId(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any())).thenReturn(user);

        User result = authService.register("123", "John", "test@test.com", "pass", User.Role.CUSTOMER);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    void testLogin_Success() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setRole(User.Role.CUSTOMER);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);

        Map<String, String> tokens = authService.login("test@test.com", "pass");

        assertNotNull(tokens.get("accessToken"));
        assertNotNull(tokens.get("refreshToken"));
    }

    @Test
    void testRefreshToken_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setRole(User.Role.CUSTOMER);

        String refreshToken = generateTestToken(userId, "refresh");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Map<String, String> newTokens = authService.refreshToken(refreshToken);

        assertNotNull(newTokens.get("accessToken"));
    }
    
    @Test
    void testValidateToken_Success() {
        UUID userId = UUID.randomUUID();
        String token = generateTestToken(userId, "access");
        
        UUID resultId = authService.validateToken(token);
        assertEquals(userId, resultId);
    }

    @Test
    void testExtractRole_Success() {
        UUID userId = UUID.randomUUID();
        String token = Jwts.builder()
                .subject(userId.toString())
                .claim("role", "CUSTOMER")
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
        
        String role = authService.extractRole(token);
        assertEquals("CUSTOMER", role);
    }

    private String generateTestToken(UUID userId, String type) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", type)
                .claim("role", "CUSTOMER")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 100000))
                .signWith(key)
                .compact();
    }
}
