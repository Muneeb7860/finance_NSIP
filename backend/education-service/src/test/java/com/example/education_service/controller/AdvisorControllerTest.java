package com.example.education_service.controller;

import com.example.education_service.model.AdvisorProfile;
import com.example.education_service.service.AdvisorService;
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
public class AdvisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdvisorService advisorService;

    @Test
    void testRegisterAdvisor() throws Exception {
        AdvisorProfile profile = new AdvisorProfile();
        profile.setId(UUID.randomUUID());
        when(advisorService.registerAdvisor(any(), any(), any(), any())).thenReturn(profile);

        mockMvc.perform(post("/api/v1/advisors/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"" + UUID.randomUUID() + "\", \"name\":\"John\", \"specialty\":\"Finance\", \"bio\":\"Bio\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testBookSession() throws Exception {
        when(advisorService.bookSession(any(), any(), any())).thenReturn(null);

        mockMvc.perform(post("/api/v1/advisors/sessions/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customerId\":\"" + UUID.randomUUID() + "\", \"advisorId\":\"" + UUID.randomUUID() + "\", \"scheduledAt\":\"2023-12-01T10:00:00\"}"))
                .andExpect(status().isOk());
    }
}
