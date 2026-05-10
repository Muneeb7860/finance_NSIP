package com.example.education_service.controller;

import com.example.education_service.model.Course;
import com.example.education_service.repository.CourseRepository;
import com.example.education_service.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
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
public class CourseControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private CourseRepository courseRepository;
    @MockBean private VideoRepository videoRepository;

    @Test
    void testGetAllCourses() throws Exception {
        when(courseRepository.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/learning/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateCourse() throws Exception {
        when(courseRepository.save(any())).thenReturn(new Course());
        mockMvc.perform(post("/api/v1/learning/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"New Course\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddVideo() throws Exception {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(new Course()));
        when(videoRepository.save(any())).thenReturn(new com.example.education_service.model.Video());

        mockMvc.perform(post("/api/v1/learning/courses/" + courseId + "/videos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"New Video\"}"))
                .andExpect(status().isOk());
    }
}
