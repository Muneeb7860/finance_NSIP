package com.example.education_service.repository;

import com.example.education_service.model.LearningStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LearningStreakRepository extends JpaRepository<LearningStreak, UUID> {
    Optional<LearningStreak> findByUserId(UUID userId);
}
