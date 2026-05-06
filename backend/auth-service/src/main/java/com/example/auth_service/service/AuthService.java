package com.example.auth_service.service;

import com.example.auth_service.model.User;
import com.example.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.crypto.SecretKey;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Injected from environment variable or Vault
    @org.springframework.beans.factory.annotation.Value("${jwt.secret:nsip-platform-secret-key-must-be-at-least-256-bits-long-for-hs256}")
    private String jwtSecret;
    
    private static final long ACCESS_TOKEN_EXPIRY_MS = 15 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY_MS = 7L * 24 * 60 * 60 * 1000; // 7 days

    /**
     * Register a new user with BCrypt-hashed password.
     * BCrypt with strength 12 means ~250ms per hash — fast enough for UX, slow enough to resist brute-force.
     */
    public User register(@NonNull String nationalId, @NonNull String fullName, @NonNull String email, @NonNull String rawPassword, @NonNull User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered.");
        }
        if (userRepository.existsByNationalId(nationalId)) {
            throw new IllegalArgumentException("National ID already registered.");
        }

        User user = new User();
        user.setNationalId(nationalId);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword)); // BCrypt hash with salt
        user.setRole(role);

        User saved = userRepository.save(user);
        log.info("Registered user: {} with role: {}", saved.getEmail(), saved.getRole());
        return saved;
    }

    /**
     * Authenticate user and return a signed JWT token with role claims.
     */
    public java.util.Map<String, String> login(@NonNull String email, @NonNull String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

        // BCrypt comparison — timing-safe, handles salt automatically
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials.");
        }

        return generateTokens(user);
    }

    public java.util.Map<String, String> refreshToken(@NonNull String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            String userId = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .getSubject();
                    
            String tokenType = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload()
                    .get("type", String.class);
                    
            if (!"refresh".equals(tokenType)) {
                throw new IllegalArgumentException("Invalid token type.");
            }

            if (userId == null) {
                throw new IllegalArgumentException("Token subject (user ID) is missing.");
            }
            UUID uuid = Objects.requireNonNull(UUID.fromString(userId));
            User user = userRepository.findById(uuid)
                    .orElseThrow(() -> new IllegalArgumentException("User not found."));

            return generateTokens(Objects.requireNonNull(user));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired refresh token.");
        }
    }

    private java.util.Map<String, String> generateTokens(@NonNull User user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String accessToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("email", user.getEmail())
                .claim("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY_MS))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(user.getId().toString())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_MS))
                .signWith(key)
                .compact();

        log.info("Tokens generated successfully for user: {}", user.getEmail());
        return java.util.Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    /**
     * Validate a JWT token and extract the user ID.
     * Throws if the token is expired, tampered, or malformed.
     */
    public UUID validateToken(@NonNull String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String userId = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return UUID.fromString(userId);
    }

    /**
     * Extract the role from a validated JWT token.
     */
    public String extractRole(@NonNull String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public Optional<User> findById(@NonNull UUID id) {
        return userRepository.findById(id);
    }
}
