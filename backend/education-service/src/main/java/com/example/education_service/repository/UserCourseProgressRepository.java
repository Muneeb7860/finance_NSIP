package com.example.education_service.repository;

import com.example.education_service.model.UserCourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, UUID> {
    Optional<UserCourseProgress> findByUserIdAndCourseId(UUID userId, UUID courseId);
    List<UserCourseProgress> findByUserId(UUID userId);
    boolean existsByUserIdAndCourseIdAndCompletedTrue(UUID userId, UUID courseId);
}
