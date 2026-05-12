package com.example.notification_engine.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TwilioSmsService {

    @Value("${twilio.account_sid:}")
    private String accountSid;

    @Value("${twilio.auth_token:}")
    private String authToken;

    @Value("${twilio.from_number:}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        if (!accountSid.isEmpty() && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio initialized successfully.");
        } else {
            log.warn("Twilio credentials missing. SMS will be simulated.");
        }
    }

    public void sendSms(String to, String content) {
        if (accountSid.isEmpty()) {
            log.info("SMS (simulated) to {}: {}", to, content);
            return;
        }

        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(fromNumber),
                    content
            ).create();
            log.info("Successfully sent SMS via Twilio: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage());
        }
    }

    public void sendWhatsApp(String to, String content) {
        if (accountSid.isEmpty()) {
            log.info("WHATSAPP (simulated) to {}: {}", to, content);
            return;
        }

        try {
            // WhatsApp requires 'whatsapp:' prefix
            String whatsappTo = to.startsWith("whatsapp:") ? to : "whatsapp:" + to;
            String whatsappFrom = fromNumber.startsWith("whatsapp:") ? fromNumber : "whatsapp:" + fromNumber;

            Message message = Message.creator(
                    new PhoneNumber(whatsappTo),
                    new PhoneNumber(whatsappFrom),
                    content
            ).create();
            log.info("Successfully sent WhatsApp via Twilio: {}", message.getSid());
        } catch (Exception e) {
            log.error("Failed to send WhatsApp via Twilio: {}", e.getMessage());
        }
    }
}
