package com.example.contribution_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "employment_id")
    private Employment employment;
    
    private String contributionMonth; // e.g., "2024-05"
    private BigDecimal amount;
    private String status; // "Pending", "Paid"
    
    @org.hibernate.annotations.CreationTimestamp
    private java.time.LocalDateTime createdAt;
}
