package com.example.event_service.controller;

import com.example.event_service.model.Event;
import com.example.event_service.service.EventService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("null")
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    void testGetLiveEvents() throws Exception {
        when(eventService.getLiveEvents()).thenReturn(List.of(new Event()));

        mockMvc.perform(get("/api/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testSubmitEvent() throws Exception {
        Event event = new Event();
        event.setId(UUID.randomUUID());
        when(eventService.submitEventProposal(any(), any(), any())).thenReturn(event);

        mockMvc.perform(post("/api/v1/events/propose")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Event\", \"createdByUserId\":\"" + UUID.randomUUID() + "\", \"organizationName\":\"Test Org\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testApproveEvent() throws Exception {
        Event event = new Event();
        event.setApprovalStatus(Event.ApprovalStatus.L1_APPROVED);
        when(eventService.approveEvent(any(), any(), any(), any(), any())).thenReturn(event);

        mockMvc.perform(patch("/api/v1/events/" + UUID.randomUUID() + "/approve")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"approverUserId\":\"" + UUID.randomUUID() + "\", \"approverName\":\"John\", \"level\":\"L1_REVIEWER\"}"))
                .andExpect(status().isOk());
    }
}
