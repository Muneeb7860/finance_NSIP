package com.example.claim_service.infrastructure.adapter.in.web;

import com.example.claim_service.application.port.in.ClaimUseCase;
import com.example.claim_service.domain.model.Claim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ClaimControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClaimUseCase claimUseCase;

    @Test
    void testSubmitClaim() throws Exception {
        Claim claim = new Claim();
        claim.setId("claim-123");
        when(claimUseCase.submitClaim(any(Claim.class))).thenReturn(claim);

        mockMvc.perform(post("/api/v2/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":\"user-1\", \"claimType\":\"EMERGENCY_RELIEF\", \"amount\":1000, \"description\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("claim-123"));
    }

    @Test
    void testGetClaims() throws Exception {
        when(claimUseCase.getClaimsByUserId("user-1")).thenReturn(List.of(new Claim()));

        mockMvc.perform(get("/api/v2/claims/user/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdateStatus() throws Exception {
        Claim claim = new Claim();
        claim.setStatus(Claim.ClaimStatus.APPROVED);
        when(claimUseCase.updateClaimStatus(any(), any())).thenReturn(claim);

        mockMvc.perform(patch("/api/v2/claims/123/status")
                .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
