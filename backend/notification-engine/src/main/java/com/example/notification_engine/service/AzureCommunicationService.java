package com.example.notification_engine.service;

import com.azure.communication.email.EmailClient;
import com.azure.communication.email.EmailClientBuilder;
import com.azure.communication.email.models.EmailMessage;
import com.azure.communication.email.models.EmailSendResult;
import com.azure.communication.sms.SmsClient;
import com.azure.communication.sms.SmsClientBuilder;
import com.azure.communication.sms.models.SmsSendResult;
import com.azure.core.util.polling.PollResponse;
import com.azure.core.util.polling.SyncPoller;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AzureCommunicationService {

    @Value("${azure.communication.connection_string:}")
    private String connectionString;

    @Value("${azure.communication.sender_email:}")
    private String senderEmail;

    @Value("${azure.communication.from_phone:}")
    private String fromPhone;

    private SmsClient smsClient;
    private EmailClient emailClient;

    @PostConstruct
    public void init() {
        if (!connectionString.isEmpty()) {
            this.smsClient = new SmsClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            this.emailClient = new EmailClientBuilder()
                    .connectionString(connectionString)
                    .buildClient();
            log.info("Azure Communication Services initialized.");
        } else {
            log.warn("Azure Communication connection string missing. ACS will be simulated.");
        }
    }

    public void sendSms(String to, String content) {
        if (smsClient == null) {
            log.info("ACS SMS (simulated) to {}: {}", to, content);
            return;
        }

        try {
            List<String> recipients = new ArrayList<>();
            recipients.add(to);
            Iterable<SmsSendResult> results = smsClient.send(fromPhone, recipients, content);
            results.forEach(result -> log.info("ACS SMS sent. Status: {}, ID: {}", result.isSuccessful(), result.getMessageId()));
        } catch (Exception e) {
            log.error("ACS SMS failed: {}", e.getMessage());
        }
    }

    public void sendEmail(String to, String subject, String content) {
        if (emailClient == null) {
            log.info("ACS EMAIL (simulated) to {}: [{}] {}", to, subject, content);
            return;
        }

        try {
            EmailMessage message = new EmailMessage();
            message.setSenderAddress(senderEmail);
            message.setSubject(subject);
            message.setBodyHtml(content);
            message.setToRecipients(to);
            
            SyncPoller<EmailSendResult, EmailSendResult> poller = emailClient.beginSend(message);
            PollResponse<EmailSendResult> result = poller.waitForCompletion();
            log.info("ACS EMAIL sent. Status: {}", result.getValue().getStatus());
        } catch (Exception e) {
            log.error("ACS EMAIL failed: {}", e.getMessage());
        }
    }
}
