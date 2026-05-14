package com.example.education_service.service;

import com.example.education_service.model.*;
import com.example.education_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class LearningServiceTest {

    @Mock private CourseRepository courseRepo;
    @Mock private UserCourseProgressRepository progressRepo;
    @Mock private CertificateRepository certRepo;
    @Mock private LearningStreakRepository streakRepo;
    @Mock private StreakService streakService;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;
    @Mock private VideoRepository videoRepo;

    @InjectMocks
    private LearningService learningService;

    @Test
    void testSubmitQuiz_FirstAttemptSuccess() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Course course = createCourse(courseId, 70, 90, 100);

        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());
        when(streakService.recordActivity(any(), any(), any(), any(), anyInt()))
            .thenReturn(Map.of("activeDaysThisWeek", 1, "weeklyStreakMet", false));

        Map<String, Object> result = learningService.submitQuiz(userId, courseId, 80, "John Doe");

        assertTrue((Boolean) result.get("passed"));
        assertEquals(110, (Integer) result.get("pointsEarned")); // 100 + (80-70)
        assertEquals(1, result.get("attempt"));
        verify(progressRepo).save(any());
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testSubmitQuiz_ThirdAttemptDiminishedReturns() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Course course = createCourse(courseId, 70, 90, 100);
        
        UserCourseProgress progress = new UserCourseProgress();
        progress.setAttemptCount(2); // Two previous attempts

        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));
        when(streakService.recordActivity(any(), any(), any(), any(), anyInt()))
            .thenReturn(Map.of("activeDaysThisWeek", 3, "weeklyStreakMet", false));

        Map<String, Object> result = learningService.submitQuiz(userId, courseId, 70, "John");

        assertEquals(0.25, result.get("multiplier"));
        assertEquals(25, (Integer) result.get("pointsEarned")); // (100+0)*0.25
        assertEquals(3, result.get("attempt"));
    }

    @Test
    void testSubmitQuiz_NoPointsForLowerScoreOnRetry() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Course course = createCourse(courseId, 70, 90, 100);
        
        UserCourseProgress progress = new UserCourseProgress();
        progress.setCompleted(true);
        progress.setQuizScore(85);
        progress.setAttemptCount(1);

        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));
        when(streakService.recordActivity(any(), any(), any(), any(), anyInt()))
            .thenReturn(Map.of("activeDaysThisWeek", 1, "weeklyStreakMet", false));

        Map<String, Object> result = learningService.submitQuiz(userId, courseId, 80, "John");

        assertEquals(0, (Integer) result.get("pointsEarned"));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void testSubmitQuiz_CertificationIssued() {
        UUID userId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        Course course = createCourse(courseId, 70, 80, 100);

        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());
        when(certRepo.existsByUserIdAndCourseId(userId, courseId)).thenReturn(false);
        when(streakService.recordActivity(any(), any(), any(), any(), anyInt()))
            .thenReturn(Map.of("activeDaysThisWeek", 1, "weeklyStreakMet", false));

        Map<String, Object> result = learningService.submitQuiz(userId, courseId, 85, "John");

        assertTrue((Boolean) result.get("certified"));
        assertNotEquals("N/A", result.get("certificate"));
        verify(certRepo).save(any());
    }

    @Test
    void testSubmitQuiz_CourseNotFound() {
        when(courseRepo.findById(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> 
            learningService.submitQuiz(UUID.randomUUID(), UUID.randomUUID(), 50, "U"));
    }

    @Test
    void testCompleteVideo_Failures() {
        UUID userId = UUID.randomUUID();
        UUID videoId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();

        // 1. Invalid Score
        assertThrows(IllegalArgumentException.class, () -> 
            learningService.completeVideoWithGamification(userId, videoId, courseId, 101));

        // 2. Video Not Found
        when(videoRepo.findById(videoId)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> 
            learningService.completeVideoWithGamification(userId, videoId, courseId, 50));

        // 3. Mismatched Course
        Course wrongCourse = new Course();
        wrongCourse.setId(UUID.randomUUID());
        Video video = new Video();
        video.setCourse(wrongCourse);
        when(videoRepo.findById(videoId)).thenReturn(Optional.of(video));
        assertThrows(IllegalArgumentException.class, () -> 
            learningService.completeVideoWithGamification(userId, videoId, courseId, 50));

        // 4. Already Completed
        Course rightCourse = new Course();
        rightCourse.setId(courseId);
        video.setCourse(rightCourse);
        UserCourseProgress progress = new UserCourseProgress();
        progress.setCompleted(true);
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(progress));
        assertThrows(IllegalArgumentException.class, () -> 
            learningService.completeVideoWithGamification(userId, videoId, courseId, 90));
    }

    @Test
    void testCompleteVideo_LowScore() {
        UUID userId = UUID.randomUUID();
        UUID videoId = UUID.randomUUID();
        UUID courseId = UUID.randomUUID();
        
        Video video = new Video();
        video.setCourse(new Course());
        video.getCourse().setId(courseId);
        
        when(videoRepo.findById(videoId)).thenReturn(Optional.of(video));
        when(progressRepo.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());

        Map<String, Object> result = learningService.completeVideoWithGamification(userId, videoId, courseId, 50);

        assertTrue(result.containsKey("error"));
        assertEquals(1, result.get("attempt"));
        verify(progressRepo).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void testGetLearningDashboard_NullStreak() {
        UUID userId = UUID.randomUUID();
        when(progressRepo.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(streakRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(certRepo.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(courseRepo.findByActiveTrue()).thenReturn(Collections.emptyList());

        Map<String, Object> result = learningService.getLearningDashboard(userId);

        assertEquals(0, result.get("currentStreak"));
        assertEquals(0L, result.get("coursesCompleted"));
    }

    @Test
    void testGetStreak_NewUser() {
        UUID userId = UUID.randomUUID();
        when(streakRepo.findByUserId(userId)).thenReturn(Optional.empty());
        
        LearningStreak result = learningService.getStreak(userId);
        
        assertEquals(userId, result.getUserId());
        assertEquals(0, result.getCurrentStreak());
    }

    @Test
    void testGetCoursesByCategory() {
        learningService.getCoursesByCategory(Course.CourseCategory.FINANCIAL_LITERACY);
        verify(courseRepo).findByCategoryAndActiveTrue(any());
    }

    private Course createCourse(UUID id, int passing, int cert, int reward) {
        Course c = new Course();
        c.setId(id);
        c.setPassingScore(passing);
        c.setCertificationScore(cert);
        c.setPointsReward(reward);
        c.setTitle("Test Course");
        c.setActive(true);
        return c;
    }
}