package com.example.review_service.service;

import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void testSubmitReview_Success() {
        UUID userId = UUID.randomUUID();
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArguments()[0]);

        Review result = reviewService.submitReview(userId, "LOAN_SAGA", 5, "Excellent!");

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void testSubmitReview_InvalidRating_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
            reviewService.submitReview(UUID.randomUUID(), "TEST", 6, "Fail")
        );
    }

    @Test
    void testGetReviewsByFeature() {
        when(reviewRepository.findByFeatureName("FEAT")).thenReturn(List.of(new Review()));
        
        List<Review> results = reviewService.getReviewsByFeature("FEAT");
        
        assertEquals(1, results.size());
    }

    @Test
    void testGetAverageRating() {
        when(reviewRepository.getAverageRatingByFeature("FEAT")).thenReturn(4.5);
        
        Double avg = reviewService.getAverageRating("FEAT");
        
        assertEquals(4.5, avg);
    }

    @Test
    void testHandleReviewPrompt() {
        // Just verify it doesn't crash
        reviewService.handleReviewPrompt("payload");
    }
}
