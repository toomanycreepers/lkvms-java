package lamart.lkvms.application.services.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lamart.lkvms.core.utilities.common.PlaintextExtractor;
import lamart.lkvms.core.utilities.exceptions.NotificationException;

@Service
public class NotificationsService {
    private JavaMailSender mailSender;
    private RestTemplate restTemplate;

    @Value("${EMAIL_HOST_USER}")
    private String fromEmail;

    @Value("${TELEGRAM_BOT_n8n_ENDPOINT}")
    private String telegramEndpoint;

    @Value("${TELEGRAM_BOT_n8n_AUTH}")
    private String telegramAuthToken;

    public void sendNotificationEmail(String recipient, String subject, String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(PlaintextExtractor.extractPlainText(message), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new NotificationException("Failed to send email notification", e);
        }
    }

    public void sendNotificationTelegram(Long chatId, String message) {
        if (message == null || message.isBlank()) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", telegramAuthToken);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("action", "inform");
        requestBody.put("chat_id", chatId);
        requestBody.put("message", message);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            restTemplate.postForEntity(telegramEndpoint, request, String.class);
        } catch (Exception e) {
            throw new NotificationException("Failed to send Telegram notification", e);
        }
    }
}
