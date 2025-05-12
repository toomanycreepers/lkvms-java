package lamart.lkvms.application.services.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.UserRepository;
import lamart.lkvms.core.utilities.common.PlaintextExtractor;

@Service
public class MassEmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private TemplateEngine templateEngine;

    @Value("${EMAIL_HOST_USER}")
    private String fromEmail;

    MassEmailService(JavaMailSender mailSender, UserRepository userRepository) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
    }

    public List<String> sendMassEmail(String templateName) {
        List<User> users = getValidUsers();
        List<String> failedRecipients = new ArrayList<>();

        for (User user : users) {
            try {
                EmailTemplate template = getTemplate(templateName);
                sendEmailToUser(user, template);
            } catch (Exception e) {
                failedRecipients.add(user.getEmail());
            }
        }

        // if (!failedRecipients.isEmpty()) {
        //     log.error("Failed sending messages to these emails: {}", 
        //         String.join(", ", failedRecipients));
        // }

        return failedRecipients;
    }

    private List<User> getValidUsers() {
        return userRepository.findByEmailIsNotNullAndIsStaffFalse();
    }

    private EmailTemplate getTemplate(String templateName) {
        return switch (templateName) {
            case "apology_for_sending_wrong_invitation" -> 
                createApologyForWrongInvitationTemplate();
            default -> throw new IllegalArgumentException("Unknown template: " + templateName);
        };
    }

    private EmailTemplate createApologyForWrongInvitationTemplate() {
        Context context = new Context();
        String subject = "Извиняемся за предыдущее сообщение";
        
        String htmlBody = templateEngine.process(
            "mass-email/apology_for_sending_wrong_invitation", 
            context
        );

        return new EmailTemplate(subject, htmlBody);
    }

    private void sendEmailToUser(User user, EmailTemplate template) 
            throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject(template.subject());
        helper.setText(PlaintextExtractor.extractPlainText(template.htmlBody()), true);

        mailSender.send(mimeMessage);
    }

    private record EmailTemplate(String subject, String htmlBody) {}
}
