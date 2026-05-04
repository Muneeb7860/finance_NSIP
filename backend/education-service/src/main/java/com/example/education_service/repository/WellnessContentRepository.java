package com.example.education_service.repository;

import com.example.education_service.model.WellnessContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WellnessContentRepository extends JpaRepository<WellnessContent, UUID> {
    List<WellnessContent> findByCategoryAndActiveTrue(WellnessContent.ContentCategory category);
}
