package com.example.event_service.repository;

import com.example.event_service.model.EventApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EventApprovalRepository extends JpaRepository<EventApproval, UUID> {
    List<EventApproval> findByEventIdOrderByActionTimestampAsc(UUID eventId);
}
