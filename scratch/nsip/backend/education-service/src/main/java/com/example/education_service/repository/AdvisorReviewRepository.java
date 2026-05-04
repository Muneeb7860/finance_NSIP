package com.example.education_service.repository;

import com.example.education_service.model.AdvisorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface AdvisorReviewRepository extends JpaRepository<AdvisorReview, UUID> {
    List<AdvisorReview> findByAdvisorIdOrderByCreatedAtDesc(UUID advisorId);
    boolean existsBySessionId(UUID sessionId);

    @Query("SELECT AVG(r.rating) FROM AdvisorReview r WHERE r.advisorId = :advisorId")
    Double getAverageRatingByAdvisorId(UUID advisorId);

    @Query("SELECT COUNT(r) FROM AdvisorReview r WHERE r.advisorId = :advisorId")
    int countByAdvisorId(UUID advisorId);
}
