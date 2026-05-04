package com.example.education_service.repository;

import com.example.education_service.model.ActivityStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ActivityStreakRepository extends JpaRepository<ActivityStreak, UUID> {
    Optional<ActivityStreak> findByUserId(UUID userId);
}
