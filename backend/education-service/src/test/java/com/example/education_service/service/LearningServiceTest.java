package com.example.education_service.service;

import com.example.education_service.model.*;
import com.example.education_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
public class LearningServiceTest {

    @Mock
    private CourseRepository courseRepo;
    @Mock
    private UserCourseProgressRepository progressRepo;
    @Mock
    private CertificateRepository certRepo;
    @Mock
    private LearningStreakRepository streakRepo;
    @Mock
    private StreakService streakService;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private VideoRepository videoRepo;

    @InjectMocks
    private LearningService learningService;

    private UUID userId;
    private UUID videoId;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        videoId = UUID.randomUUID();
        courseId = UUID.randomUUID();
    }

    @Test
    void testCompleteVideoWithGamification_Success() {
        // Arrange
        Video video = new Video();
        Course course = new Course();
        course.setId(courseId);
        video.setCourse(course);

        UserCourseProgress progress = new UserCourseProgress();
        progress.setUserId(userId);
        progress.setCourseId(courseId);
        progress.setCompleted(false);
        progress.setQuizScore(0);
        progress.setAttemptCount(0);

        when(videoRepo.findById(videoId)).thenReturn(Optional.of(video));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));

        // Act
        Map<String, Object> result = learningService.completeVideoWithGamification(userId, videoId, courseId, 85);

        // Assert
        assertEquals("Congratulations! You scored 85% and earned 65 points!", result.get("message"));
        assertEquals(65, result.get("points"));
        assertEquals(courseId, result.get("courseId"));
        verify(progressRepo).save(progress);
        verify(kafkaTemplate).send(eq("gamification.events"), anyString());
    }

    @Test
    void testCompleteVideoWithGamification_AlreadyCompleted() {
        // Arrange
        Video video = new Video();
        Course course = new Course();
        course.setId(courseId);
        video.setCourse(course);

        UserCourseProgress progress = new UserCourseProgress();
        progress.setCompleted(true);

        when(videoRepo.findById(videoId)).thenReturn(Optional.of(video));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> learningService.completeVideoWithGamification(userId, videoId, courseId, 85));
        assertEquals("Course already completed. Points were previously awarded.", exception.getMessage());
    }

    @Test
    void testCompleteVideoWithGamification_QuiFailed() {
        // Arrange
        Video video = new Video();
        Course course = new Course();
        course.setId(courseId);
        video.setCourse(course);

        UserCourseProgress progress = new UserCourseProgress();
        progress.setCompleted(false);

        when(videoRepo.findById(videoId)).thenReturn(Optional.of(video));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));

        // Act
        Map<String, Object> result = learningService.completeVideoWithGamification(userId, videoId, courseId, 65);

        // Assert
        assertTrue(result.containsKey("error"));
        assertTrue(result.get("error").toString().contains("Quiz failed"));
        verify(progressRepo).save(progress);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void testCompleteVideoWithGamification_InvalidScore() {
        // Arrange
        Video video = new Video();
        Course course = new Course();
        course.setId(courseId);
        video.setCourse(course);

        when(videoRepo.findById(videoId)).thenReturn(Optional.of(video));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> learningService.completeVideoWithGamification(userId, videoId, courseId, 150));
        assertEquals("Quiz score must be between 0 and 100.", exception.getMessage());
    }
}