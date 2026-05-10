package com.example.education_service.service;

import com.example.education_service.model.ActivityLog;
import com.example.education_service.model.ActivityStreak;
import com.example.education_service.repository.ActivityLogRepository;
import com.example.education_service.repository.ActivityStreakRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class StreakServiceTest {

    @Mock private ActivityStreakRepository streakRepo;
    @Mock private ActivityLogRepository logRepo;
    @Mock private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private StreakService streakService;

    @Test
    void testRecordActivity_NewStreak() {
        UUID userId = UUID.randomUUID();
        when(streakRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(streakRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> result = streakService.recordActivity(userId, ActivityLog.ActivityType.QUIZ_COMPLETED, "ref", "details", 10);

        assertNotNull(result);
        assertEquals(1, result.get("activeDaysThisWeek"));
        verify(logRepo).save(any());
        verify(streakRepo).save(any());
    }

    @Test
    void testRecordActivity_RolloverMet() {
        UUID userId = UUID.randomUUID();
        ActivityStreak streak = new ActivityStreak();
        streak.setUserId(userId);
        streak.setLastTrackedWeekNumber(1); // Old week
        streak.setLastTrackedYear(2020);
        streak.recordDay(1);
        streak.recordDay(2);
        streak.recordDay(3);
        streak.recordDay(4); // 4 days met

        when(streakRepo.findByUserId(userId)).thenReturn(Optional.of(streak));
        when(streakRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> result = streakService.recordActivity(userId, ActivityLog.ActivityType.QUIZ_COMPLETED, "ref", "details", 10);

        assertEquals(1, result.get("currentWeeklyStreak"));
        verify(kafkaTemplate).send(anyString(), anyString());
    }

    @Test
    void testRecordActivity_RolloverBroken() {
        UUID userId = UUID.randomUUID();
        ActivityStreak streak = new ActivityStreak();
        streak.setUserId(userId);
        streak.setLastTrackedWeekNumber(1);
        streak.setLastTrackedYear(2020);
        streak.setCurrentWeeklyStreak(5);
        streak.recordDay(1); // Only 1 day met

        when(streakRepo.findByUserId(userId)).thenReturn(Optional.of(streak));
        when(streakRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Map<String, Object> result = streakService.recordActivity(userId, ActivityLog.ActivityType.QUIZ_COMPLETED, "ref", "details", 10);

        assertEquals(0, result.get("currentWeeklyStreak"));
    }

    @Test
    void testGetStreakDashboard() {
        UUID userId = UUID.randomUUID();
        when(streakRepo.findByUserId(userId)).thenReturn(Optional.of(new ActivityStreak()));
        when(logRepo.findTop20ByUserIdOrderByTimestampDesc(userId)).thenReturn(java.util.List.of());

        Map<String, Object> result = streakService.getStreakDashboard(userId);

        assertNotNull(result);
        assertTrue(result.containsKey("recentActivity"));
    }
}
