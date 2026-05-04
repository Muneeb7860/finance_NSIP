package com.example.education_service.repository;

import com.example.education_service.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {
    List<ActivityLog> findByUserIdOrderByTimestampDesc(UUID userId);
    List<ActivityLog> findTop20ByUserIdOrderByTimestampDesc(UUID userId);
}
