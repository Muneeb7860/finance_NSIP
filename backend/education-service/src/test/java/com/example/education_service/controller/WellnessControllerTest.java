package com.example.education_service.controller;

import com.example.education_service.repository.ChronicCareEnrollmentRepository;
import com.example.education_service.repository.WellnessContentRepository;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("null")
public class WellnessControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private WellnessContentRepository contentRepo;
    @MockBean private ChronicCareEnrollmentRepository enrollmentRepo;

    @Test
    void testGetFitnessTips() throws Exception {
        when(contentRepo.findByCategoryAndActiveTrue(any())).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/wellness/fitness-tips"))
                .andExpect(status().isOk());
    }

    @Test
    void testEnrollInProgram() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID programId = UUID.randomUUID();
        when(enrollmentRepo.existsByUserIdAndProgramId(userId, programId)).thenReturn(false);
        when(enrollmentRepo.save(any())).thenReturn(new com.example.education_service.model.ChronicCareEnrollment());

        String json = String.format("{\"userId\":\"%s\", \"programId\":\"%s\"}", userId, programId);

        mockMvc.perform(post("/api/v1/wellness/chronic-care/enroll")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }
}
