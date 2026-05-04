package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "wellness_registrations")
public class WellnessRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String conditionName;
    private String assistanceType; // e.g., HOME_NURSING, TELE_CONSULT
    private String status; // ACTIVE, PENDING, COMPLETED
    
    private LocalDateTime registeredAt;
    private LocalDateTime lastVisitAt;
}
