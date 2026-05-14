package com.example.education_service.service;

import com.example.education_service.model.ChronicCareEnrollment;
import com.example.education_service.model.WellnessRegistration;
import com.example.education_service.repository.ChronicCareEnrollmentRepository;
import com.example.education_service.repository.WellnessRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class WellnessService {

    private final ChronicCareEnrollmentRepository repository;
    private final WellnessRegistrationRepository wellnessRegistrationRepository;

    public WellnessRegistration registerCondition(String userId, String condition, String assistanceType) {
        WellnessRegistration reg = new WellnessRegistration();
        reg.setUserId(userId);
        reg.setConditionName(condition);
        reg.setAssistanceType(assistanceType);
        reg.setStatus("PENDING_APPROVAL");
        reg.setRegisteredAt(LocalDateTime.now());
        return wellnessRegistrationRepository.save(reg);
    }

    public List<WellnessRegistration> getUserRegistrations(String userId) {
        return wellnessRegistrationRepository.findByUserId(userId);
    }

    public void updateVisit(Long registrationId) {
        wellnessRegistrationRepository.findById(registrationId).ifPresent(reg -> {
            reg.setLastVisitAt(LocalDateTime.now());
            wellnessRegistrationRepository.save(reg);
        });
    }

    public ChronicCareEnrollment enrollInProgram(UUID userId, UUID programId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        log.info("Enrolling user {} in chronic care program {}", userId, programId);
        
        ChronicCareEnrollment enrollment = new ChronicCareEnrollment();
        enrollment.setUserId(userId);
        enrollment.setProgramId(programId);
        enrollment.setStatus(ChronicCareEnrollment.EnrollmentStatus.ACTIVE);
        enrollment.setEnrolledAt(LocalDateTime.now());
        
        return repository.save(enrollment);
    }

    public List<ChronicCareEnrollment> getUserEnrollments(UUID userId) {
        return repository.findByUserId(userId);
    }

    public void trackTeleconsult(UUID enrollmentId) {
        repository.findById(enrollmentId).ifPresent(enrollment -> {
            enrollment.setTeleconsultsCompleted(enrollment.getTeleconsultsCompleted() + 1);
            repository.save(enrollment);
            log.info("Tracked teleconsult for enrollment {}", enrollmentId);
        });
    }

    public void scheduleHomeVisit(UUID enrollmentId, LocalDateTime visitTime) {
        repository.findById(enrollmentId).ifPresent(enrollment -> {
            enrollment.setNextHomeVisit(visitTime);
            repository.save(enrollment);
            log.info("Scheduled home visit for enrollment {} at {}", enrollmentId, visitTime);
        });
    }
}
