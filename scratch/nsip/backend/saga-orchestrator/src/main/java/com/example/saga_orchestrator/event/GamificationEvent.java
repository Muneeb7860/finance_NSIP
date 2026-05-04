package com.example.saga_orchestrator.event;

import java.io.Serializable;
import java.util.UUID;

/**
 * Typed event for gamification (course completion, event attendance).
 * Consumed by the rewards-service to award points.
 */
public class GamificationEvent implements Serializable {
    private UUID userId;
    private UUID resourceId;  // courseId or eventId
    private int pointsEarned;
    private String eventType; // COURSE_COMPLETED, EVENT_ATTENDED
    private boolean certified;

    public GamificationEvent() {}

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public boolean isCertified() { return certified; }
    public void setCertified(boolean certified) { this.certified = certified; }
}
