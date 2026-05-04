package com.example.education_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    
    private String title;
    private String embeddedUrl; // e.g., YouTube URL
    private Integer pointsReward;
}
