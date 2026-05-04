package com.example.notification_engine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationDispatcher {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * Kafka listener: Central notification dispatcher.
     * Routes messages to the appropriate omnichannel endpoint.
     */
    @KafkaListener(topics = "notification.command.send", groupId = "notification-group")
    public void handleNotification(String payload) {
        log.info("Notification received: {}", payload);

        // Dispatch to all active channels
        sendEmail(payload);
        sendWhatsApp(payload);
        sendSms(payload);
        sendSocialDM(payload);
    }

    /**
     * Send email notification via Spring Boot JavaMailSender.
     */
    private void sendEmail(String payload) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo("user@example.com"); // Would parse from payload
                message.setSubject("NSIP Platform Notification");
                message.setText("You have a new notification: " + payload);
                mailSender.send(message);
                log.info("EMAIL sent successfully.");
            } else {
                log.info("EMAIL (simulated): {}", payload);
            }
        } catch (Exception e) {
            log.error("EMAIL failed: {}", e.getMessage());
        }
    }

    /**
     * Send WhatsApp notification via WhatsApp Business API.
     * In production, integrate with Meta's WhatsApp Cloud API.
     */
    private void sendWhatsApp(String payload) {
        log.info("WHATSAPP (simulated): {}", payload);
        // POST https://graph.facebook.com/v17.0/{phone-number-id}/messages
    }

    /**
     * Send SMS notification.
     * In production, integrate with Twilio or AWS SNS.
     */
    private void sendSms(String payload) {
        log.info("SMS (simulated): {}", payload);
        // Twilio API call
    }

    /**
     * Send Direct Message on social platforms (X, Instagram, Snapchat).
     * In production, integrate with each platform's DM API.
     */
    private void sendSocialDM(String payload) {
        log.info("SOCIAL DM (simulated to X/Instagram/Snapchat): {}", payload);
        // X API v2: POST /2/dm_conversations
        // Instagram Graph API
    }
}
