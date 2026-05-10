package com.example.review_service.controller;

import com.example.review_service.model.Review;
import com.example.review_service.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("null")
public class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @Test
    void testSubmitReview() throws Exception {
        Review review = new Review();
        review.setId(UUID.randomUUID());
        when(reviewService.submitReview(any(UUID.class), anyString(), anyInt(), anyString())).thenReturn(review);

        mockMvc.perform(post("/api/v1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + UUID.randomUUID() + "\", \"featureName\":\"FEAT\", \"rating\":5, \"comment\":\"Great!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testGetReviews() throws Exception {
        when(reviewService.getReviewsByFeature("FEAT")).thenReturn(List.of(new Review()));

        mockMvc.perform(get("/api/v1/reviews/feature/FEAT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetAverage() throws Exception {
        when(reviewService.getAverageRating("FEAT")).thenReturn(4.2);

        mockMvc.perform(get("/api/v1/reviews/feature/FEAT/average"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(4.2));
    }
}
