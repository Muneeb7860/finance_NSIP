package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Certificates earned by users for completing courses with 90%+ quiz scores.
 * Each certificate has a unique verifiable ID.
 */
@Entity
@Table(name = "certificates")
@Data
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID courseId;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false, unique = true)
    private String certificateNumber; // e.g. "NSIP-CERT-2026-00042"

    private int quizScore;
    private LocalDateTime issuedAt = LocalDateTime.now();
    private LocalDateTime expiresAt; // null = never expires

    @Enumerated(EnumType.STRING)
    private CertificateType type;

    public enum CertificateType {
        FINANCIAL_LITERACY,
        INVESTMENT_BASICS,
        INSURANCE_FUNDAMENTALS,
        TAX_PLANNING,
        RETIREMENT_PLANNING
    }
}
