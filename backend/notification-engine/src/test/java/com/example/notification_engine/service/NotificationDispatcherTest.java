package com.example.notification_engine.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class NotificationDispatcherTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationDispatcher notificationDispatcher;

    @Test
    void testHandleNotification_Success() {
        String payload = "{\"userId\":\"user-1\", \"message\":\"Test\"}";
        
        notificationDispatcher.handleNotification(payload);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testHandleNotification_MailFails() {
        String payload = "{\"userId\":\"user-1\", \"message\":\"Test\"}";
        doThrow(new RuntimeException("Mail server down")).when(mailSender).send(any(SimpleMailMessage.class));

        // Should not throw exception
        notificationDispatcher.handleNotification(payload);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
