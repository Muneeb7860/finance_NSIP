package com.example.notification_engine.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
@Slf4j
public class FirebasePushService {

    @Value("${firebase.config.path:}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        if (firebaseConfigPath == null || firebaseConfigPath.isEmpty()) {
            log.warn("Firebase config path not provided. Push notifications will be simulated.");
            return;
        }
        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            log.info("Firebase Application initialized successfully.");
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
        }
    }

    public void sendPushNotification(String token, String title, String body) {
        if (FirebaseApp.getApps().isEmpty()) {
            log.info("PUSH (simulated) to token {}: [{}] {}", token, title, body);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent FCM message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM message: {}", e.getMessage());
        }
    }
}
