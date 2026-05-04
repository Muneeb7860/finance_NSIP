package com.example.education_service.repository;

import com.example.education_service.model.WellnessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WellnessRegistrationRepository extends JpaRepository<WellnessRegistration, Long> {
    List<WellnessRegistration> findByUserId(String userId);
}
