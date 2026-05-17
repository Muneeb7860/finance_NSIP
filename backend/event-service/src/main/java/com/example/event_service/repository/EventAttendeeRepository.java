package com.example.event_service.repository;

import com.example.event_service.model.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EventAttendeeRepository extends JpaRepository<EventAttendee, UUID> {
    List<EventAttendee> findByEventId(UUID eventId);
    List<EventAttendee> findByUserId(UUID userId);
    boolean existsByEventIdAndUserId(UUID eventId, UUID userId);
    java.util.Optional<EventAttendee> findByEventIdAndUserId(UUID eventId, UUID userId);
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT ea.eventId FROM EventAttendee ea WHERE ea.userId = :userId")
    List<UUID> findEventIdsByUserId(@org.springframework.data.repository.query.Param("userId") UUID userId);
}
