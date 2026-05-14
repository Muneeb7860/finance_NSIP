package com.example.auth_service.controller;

import com.example.auth_service.model.User;
import com.example.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration and JWT login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Register a new user", description = "Creates a new account with BCrypt-hashed password. Returns userId and role.")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Email or National ID already exists")
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String nationalId = Objects.requireNonNull(body.get("nationalId"), "National ID is required");
            String fullName = Objects.requireNonNull(body.get("fullName"), "Full name is required");
            String email = Objects.requireNonNull(body.get("email"), "Email is required");
            String password = Objects.requireNonNull(body.get("password"), "Password is required");
            
            User user = authService.register(
                    nationalId,
                    fullName,
                    email,
                    password,
                    User.Role.valueOf(body.getOrDefault("role", "CUSTOMER"))
            );
            return ResponseEntity.ok(Map.of("userId", user.getId(), "role", user.getRole()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Login and receive JWT token", description = "Validates credentials and returns short-lived access token and refresh token.")
    @ApiResponse(responseCode = "200", description = "Login successful — Tokens returned")
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String email = Objects.requireNonNull(body.get("email"), "Email is required");
            String password = Objects.requireNonNull(body.get("password"), "Password is required");
            Map<String, String> tokens = authService.login(email, password);
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Refresh JWT Token", description = "Exchanges a valid refresh token for a new pair of access/refresh tokens.")
    @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        try {
            String refreshToken = Objects.requireNonNull(body.get("refreshToken"), "Refresh token is required");
            Map<String, String> tokens = authService.refreshToken(refreshToken);
            return ResponseEntity.ok(tokens);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Get user profile by ID", description = "Returns user details (name, email, role). Internal use only.")
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable @org.springframework.lang.NonNull String userId) {
        return authService.getUserById(userId)
                .map(user -> ResponseEntity.ok(Map.of(
                        "userId", user.getId(),
                        "fullName", user.getFullName(),
                        "email", user.getEmail(),
                        "role", user.getRole()
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Generate LiveKit Voice Token", description = "Generates a short-lived token for real-time voice interaction.")
    @GetMapping("/livekit-token")
    public ResponseEntity<?> getLiveKitToken(@RequestParam String userId, @RequestParam(defaultValue = "hafida-room") String roomName) {
        String token = authService.generateLiveKitToken(userId, roomName);
        if (token.isEmpty()) {
            return ResponseEntity.internalServerError().body(Map.of("error", "LiveKit configuration missing on server"));
        }
        return ResponseEntity.ok(Map.of("token", token));
    }
}
