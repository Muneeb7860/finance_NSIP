package com.example.education_service.repository;

import com.example.education_service.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryCoverageTest {

    @Autowired private ActivityLogRepository activityLogRepository;
    @Autowired private ActivityStreakRepository activityStreakRepository;
    @Autowired private AdvisorProfileRepository advisorProfileRepository;
    @Autowired private WellnessRegistrationRepository wellnessRegistrationRepository;
    @Autowired private CourseRepository courseRepository;
    @Autowired private ChronicCareEnrollmentRepository chronicCareRepository;

    @Test
    void testRepositories() {
        UUID userId = UUID.randomUUID();
        
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setActivityType(ActivityLog.ActivityType.QUIZ_COMPLETED);
        assertNotNull(activityLogRepository.save(log));

        ActivityStreak streak = new ActivityStreak();
        streak.setUserId(userId);
        streak.setLastActivityDate(LocalDate.now());
        assertNotNull(activityStreakRepository.save(streak));

        AdvisorProfile profile = new AdvisorProfile();
        profile.setUserId(userId);
        profile.setName("Advisor");
        profile.setSpecialty("Finance");
        assertNotNull(advisorProfileRepository.save(profile));

        Course course = new Course();
        course.setTitle("Course");
        assertNotNull(courseRepository.save(course));

        WellnessRegistration wellness = new WellnessRegistration();
        wellness.setUserId(userId.toString());
        assertNotNull(wellnessRegistrationRepository.save(wellness));

        ChronicCareEnrollment chronic = new ChronicCareEnrollment();
        chronic.setUserId(userId);
        chronic.setProgramId(UUID.randomUUID());
        assertNotNull(chronicCareRepository.save(chronic));
    }
}
