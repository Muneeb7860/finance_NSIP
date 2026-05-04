package com.example.saga_orchestrator.event;

import java.io.Serializable;
import java.util.UUID;

/**
 * Typed event for notifications sent through the notification engine.
 * Replaces hardcoded JSON strings with a structured, validated object.
 */
public class NotificationEvent implements Serializable {
    private UUID userId;
    private String status;   // SUCCESS, FAILED, INFO
    private String message;
    private String channel;  // EMAIL, WHATSAPP, SMS, ALL

    public NotificationEvent() {}

    public NotificationEvent(UUID userId, String status, String message) {
        this.userId = userId;
        this.status = status;
        this.message = message;
        this.channel = "ALL";
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
