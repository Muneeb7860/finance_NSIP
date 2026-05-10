package com.example.education_service.controller;

import com.example.education_service.service.StreakService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
public class StreakControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private StreakService streakService;

    @Test
    void testRecordActivity() throws Exception {
        UUID userId = UUID.randomUUID();
        when(streakService.recordActivity(any(), any(), anyString(), anyString(), anyInt())).thenReturn(Map.of("status", "SUCCESS"));

        String json = String.format("{\"userId\":\"%s\", \"activityType\":\"QUIZ_COMPLETED\"}", userId);

        mockMvc.perform(post("/api/v1/streaks/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    void testGetStreakDashboard() throws Exception {
        UUID userId = UUID.randomUUID();
        when(streakService.getStreakDashboard(userId)).thenReturn(Map.of());
        mockMvc.perform(get("/api/v1/streaks/dashboard/" + userId))
                .andExpect(status().isOk());
    }
}
