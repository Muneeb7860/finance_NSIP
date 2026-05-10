package com.example.rewards_service.controller;

import com.example.rewards_service.model.AdvisorSession;
import com.example.rewards_service.service.RewardsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("null")
public class RewardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardsService rewardsService;

    @Test
    void testGetBalance() throws Exception {
        UUID userId = UUID.randomUUID();
        when(rewardsService.getBalance(userId)).thenReturn(1500);

        mockMvc.perform(get("/api/v1/rewards/balance/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void testBookSession() throws Exception {
        AdvisorSession session = new AdvisorSession();
        session.setId(UUID.randomUUID());
        when(rewardsService.bookSession(any(), any(), any())).thenReturn(session);

        mockMvc.perform(post("/api/v1/rewards/sessions/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + UUID.randomUUID() + "\", \"advisorId\":\"" + UUID.randomUUID() + "\", \"scheduledTime\":\"2023-12-01T10:00:00\"}"))
                .andExpect(status().isOk());
    }
}
