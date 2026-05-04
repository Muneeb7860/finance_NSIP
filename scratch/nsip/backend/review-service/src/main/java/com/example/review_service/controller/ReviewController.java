package com.example.review_service.controller;

import com.example.review_service.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> submitReview(@RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(reviewService.submitReview(
                    UUID.fromString(body.get("userId")),
                    body.get("featureName"),
                    Integer.parseInt(body.get("rating")),
                    body.get("comment")
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/feature/{featureName}")
    public ResponseEntity<?> getReviews(@PathVariable String featureName) {
        return ResponseEntity.ok(reviewService.getReviewsByFeature(featureName));
    }

    @GetMapping("/feature/{featureName}/average")
    public ResponseEntity<?> getAverageRating(@PathVariable String featureName) {
        return ResponseEntity.ok(Map.of("featureName", featureName, "averageRating", reviewService.getAverageRating(featureName)));
    }
}
