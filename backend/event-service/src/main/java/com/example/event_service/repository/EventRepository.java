package com.example.event_service.repository;

import com.example.event_service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByType(Event.EventType type);
    List<Event> findByCategory(Event.EventCategory category);
    List<Event> findByCreatedByUserId(UUID userId);

    /** Only LIVE events are visible to contributors/customers. */
    List<Event> findByApprovalStatus(Event.ApprovalStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT e FROM Event e WHERE e.id IN :eventIds")
    List<Event> findByIdIn(@org.springframework.data.repository.query.Param("eventIds") List<UUID> eventIds);
}
