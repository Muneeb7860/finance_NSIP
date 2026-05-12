package com.example.notification_engine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebSocketNotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Send a notification to a specific user via WebSocket.
     * 
     * @param userId The ID of the user to receive the notification.
     * @param payload The notification content.
     */
    public void sendToUser(String userId, String payload) {
        log.info("Sending WebSocket notification to user {}: {}", userId, payload);
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", payload);
    }

    /**
     * Broadcast a notification to all connected users.
     * 
     * @param payload The notification content.
     */
    public void broadcast(String payload) {
        log.info("Broadcasting WebSocket notification: {}", payload);
        messagingTemplate.convertAndSend("/topic/notifications", payload);
    }
}
