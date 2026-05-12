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

    @Autowired
    private FirebasePushService pushService;

    @Autowired
    private TwilioSmsService twilioService;

    @Autowired
    private AzureCommunicationService azureService;

    @Autowired
    private WebSocketNotificationService webSocketService;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    /**
     * Kafka listener: Central notification dispatcher.
     * Routes messages to the appropriate omnichannel endpoint.
     */
    @KafkaListener(topics = "notification.command.send", groupId = "notification-group")
    public void handleNotification(String payload) {
        log.info("Notification received: {}", payload);

        // In a real app, we would parse JSON: {userId, message, channels:[], targetToken, phoneNumber, email}
        // For now, we dispatch to all active channels using the generic payload
        
        String recipientEmail = "user@example.com"; // Placeholder: extract from payload/user-service
        String phoneNumber = "+1234567890";      // Placeholder
        String fcmToken = "dummy-token";          // Placeholder

        // 1. Email (Priority: ACS -> SMTP)
        azureService.sendEmail(recipientEmail, "NSIP Notification", payload);
        sendEmail(recipientEmail, payload);

        // 2. SMS (Priority: ACS -> Twilio)
        azureService.sendSms(phoneNumber, payload);
        twilioService.sendSms(phoneNumber, payload);

        // 3. WhatsApp (Twilio)
        twilioService.sendWhatsApp(phoneNumber, payload);

        // 4. Push (FCM)
        pushService.sendPushNotification(fcmToken, "NSIP Platform", payload);
        
        // 5. WebSocket (Real-time in-app)
        webSocketService.sendToUser("user-123", payload); // Placeholder user-id
        
        // 6. Social DM (Simulated)
        sendSocialDM(payload);
    }

    /**
     * Send email notification via Spring Boot JavaMailSender.
     */
    private void sendEmail(String to, String content) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject("NSIP Platform Notification");
                message.setText("You have a new notification: " + content);
                mailSender.send(message);
                log.info("EMAIL sent successfully to {}", to);
            } else {
                log.info("EMAIL (simulated) to {}: {}", to, content);
            }
        } catch (Exception e) {
            log.error("EMAIL failed: {}", e.getMessage());
        }
    }

    /**
     * Send Direct Message on social platforms (Simulated).
     */
    private void sendSocialDM(String payload) {
        log.info("SOCIAL DM (simulated to X/Instagram/Snapchat): {}", payload);
    }
}
