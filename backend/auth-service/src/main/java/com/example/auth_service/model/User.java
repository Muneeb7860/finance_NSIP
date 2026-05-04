package com.example.auth_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String nationalId;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean mfaEnabled = false;
    private String mfaSecret; // For OTP/Passcode
    private String preferredMfaType; // NAFATH, BIOMETRIC, OTP, etc.

    public enum Role {
        CUSTOMER, EMPLOYER, ADMIN, ADVISOR
    }
}
