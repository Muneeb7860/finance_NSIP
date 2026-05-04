package com.example.review_service.repository;

import com.example.review_service.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByFeatureName(String featureName);
    List<Review> findByUserId(UUID userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.featureName = :featureName")
    Double getAverageRatingByFeature(String featureName);
}
