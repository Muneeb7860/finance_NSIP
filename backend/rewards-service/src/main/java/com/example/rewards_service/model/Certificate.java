package com.example.rewards_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

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
    private String title;

    @Column(nullable = false)
    private String issuer = "NSIP National Platform";

    @Column(nullable = false)
    private LocalDateTime issuedAt = LocalDateTime.now();

    private String certificateUrl;
    
    @Enumerated(EnumType.STRING)
    private CertificateType type;

    public enum CertificateType {
        ACADEMIC, PROFESSIONAL, PARTICIPATION, ACHIEVEMENT
    }
}
