package com.example.education_service.controller;

import com.example.education_service.service.LearningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("null")
public class LearningControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private LearningService learningService;

    @Test
    void testGetCourses() throws Exception {
        when(learningService.getAllCourses()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/learning/list"))
                .andExpect(status().isOk());
    }

    @Test
    void testSubmitQuiz() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        when(learningService.submitQuiz(any(), any(), anyInt(), anyString())).thenReturn(Map.of("passed", true));

        String json = String.format("{\"userId\":\"%s\", \"courseId\":\"%s\", \"score\":\"85\"}", userId, courseId);

        mockMvc.perform(post("/api/v1/learning/quiz/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testGetDashboard() throws Exception {
        UUID userId = UUID.randomUUID();
        when(learningService.getLearningDashboard(userId)).thenReturn(Map.of());
        mockMvc.perform(get("/api/v1/learning/dashboard/" + userId))
                .andExpect(status().isOk());
    }
}
