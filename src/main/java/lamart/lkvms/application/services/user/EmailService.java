package lamart.lkvms.application.services.user;

import java.util.Locale;
import java.util.UUID;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lamart.lkvms.core.entities.user.User;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final CodeService codeService;
    private final IpLocationService ipLocationService;

    @Value("${EMAIL_HOST_USER}")
    private String fromEmail;

    @Value("${FRONTEND_URL}") 
    private String frontendUrl;

    @Value("${app.registration-link}")
    private String registrationLink;

    @Value("${app.base-url}")
    private String baseUrl;

    EmailService(JavaMailSender mailSender, TemplateEngine templateEngine, CodeService codeService, IpLocationService ipLocationService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.codeService = codeService;
        this.ipLocationService = ipLocationService;
    }

    private static String extractPlainText(String html) {
        Document doc = Jsoup.parse(html);
        return doc.text();
    }

    public boolean sendConfirmationEmail(User user, String ipAddress, String confirmationCode) {
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable("confirmationCode", confirmationCode);
            context.setVariable("username", user.getUsername());
            context.setVariable("ipAddress", ipAddress);

            String htmlBody = templateEngine.process("emails/confirmation_email", context);
            String plainText = extractPlainText(htmlBody);

            return sendEmail(
                user.getEmail(),
                "Подтверждение входа с нового устройства",
                plainText,
                htmlBody
            );
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendConfirmationEmail(String email, int confirmationCode) {
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable("confirmationCode", confirmationCode);
            
            String htmlBody = templateEngine.process("emails/registration_confirmation_email", context);
            String plainText = extractPlainText(htmlBody);

            return sendEmail(
                email,
                "Подтверждение регистрации",
                plainText,
                htmlBody
            );
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendPasswordResetEmail(String email, String ipAddress) {
        try {
            String location = ipLocationService.getLocation(ipAddress).orElse(null);
            String resetCode = codeService.getOrCreatePasswordResetCode(email);

            Context context = new Context(Locale.getDefault());
            context.setVariable("email", email);
            context.setVariable("ipAddress", ipAddress);
            context.setVariable("location", location);
            context.setVariable("resetCode", resetCode);
            context.setVariable("siteLink", frontendUrl);

            String htmlBody = templateEngine.process("emails/password-reset-email", context);
            String plainText = extractPlainText(htmlBody);

            return sendEmail(
                email,
                "Восстановление пароля",
                plainText,
                htmlBody
            );
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendInviteEmail(User user, UUID inviteId){
        try {
            Context context = new Context(Locale.getDefault());
            context.setVariable("username", user.getUsername());
            context.setVariable("registrationLink", registrationLink);
            context.setVariable("unsubscribeLink", buildUnsubLink(inviteId.toString()));

            String htmlBody = templateEngine.process("emails/invitation_to_register", context);
            String plainText = extractPlainText(htmlBody);

            return sendEmail(
                user.getEmail(),
                "Приглашение на регистрацию",
                plainText,
                htmlBody
            );
        } catch (Exception e) {
            return false;
        }
    }
    

    private boolean sendEmail(String to, String subject, String plainText, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(plainText, htmlBody);

            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    private String buildUnsubLink(String inviteId){
        return UriComponentsBuilder.fromUriString(baseUrl)
            .path("/api/users/unsubscribe/")
            .path(inviteId)
            .toUriString();
    }
}