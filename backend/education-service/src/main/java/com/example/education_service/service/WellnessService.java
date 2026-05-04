package com.example.education_service.service;

import com.example.education_service.model.WellnessRegistration;
import com.example.education_service.repository.WellnessRegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class WellnessService {

    private final WellnessRegistrationRepository repository;

    public WellnessRegistration registerCondition(String userId, String conditionName, String assistanceType) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        log.info("Registering wellness condition for user {}: {}", userId, conditionName);
        WellnessRegistration registration = new WellnessRegistration();
        registration.setUserId(userId);
        registration.setConditionName(conditionName);
        registration.setAssistanceType(assistanceType);
        registration.setStatus("PENDING_APPROVAL");
        registration.setRegisteredAt(LocalDateTime.now());
        return repository.save(registration);
    }

    public List<WellnessRegistration> getUserRegistrations(String userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return repository.findByUserId(userId);
    }

    public void updateVisit(Long registrationId) {
        Objects.requireNonNull(registrationId, "Registration ID cannot be null");
        repository.findById(registrationId).ifPresent(reg -> {
            reg.setLastVisitAt(LocalDateTime.now());
            repository.save(reg);
            log.info("Updated last visit for wellness registration {}", registrationId);
        });
    }
}
