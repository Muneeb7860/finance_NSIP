package com.example.review_service.service;

import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public Review submitReview(UUID userId, String featureName, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setFeatureName(featureName);
        review.setRating(rating);
        review.setComment(comment);

        Review saved = reviewRepository.save(review);
        log.info("Review submitted: {} for feature '{}' rating={}", saved.getId(), featureName, rating);
        return saved;
    }

    public List<Review> getReviewsByFeature(String featureName) {
        return reviewRepository.findByFeatureName(featureName);
    }

    public Double getAverageRating(String featureName) {
        return reviewRepository.getAverageRatingByFeature(featureName);
    }

    /**
     * Kafka listener: Triggered by Saga completion to prompt for a review.
     */
    @KafkaListener(topics = "review.command.prompt", groupId = "review-group")
    public void handleReviewPrompt(String payload) {
        log.info("Review prompt triggered for payload: {}", payload);
        // In production, notify user to leave a review via notification-engine
    }
}
