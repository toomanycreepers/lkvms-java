package lamart.lkvms.application.services.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lamart.lkvms.core.entities.user.EmailConfirmation;
import lamart.lkvms.core.repositories.EmailConfirmationRepository;

@Service
public class EmailConfirmationService {

    private static final int CONFIRMATION_CODE_MIN = 100000;
    private static final int CONFIRMATION_CODE_MAX = 999999;
    private static final int CONFIRMATION_EMAIL_LIFETIME_MINUTES = 360;

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final EmailService emailService;
    private final Random random = new Random();

    EmailConfirmationService(EmailConfirmationRepository emailConfirmationRepository, EmailService emailService) {
        this.emailConfirmationRepository = emailConfirmationRepository;
        this.emailService = emailService;
    }

    public EmailConfirmation getEmailConfirmation(UUID confirmationId, boolean raiseException) {
        Optional<EmailConfirmation> confirmation = emailConfirmationRepository.findById(confirmationId);
        if (raiseException && confirmation.isEmpty()) {
            throw new EntityNotFoundException("No email confirmation with this id.");
        }
        return confirmation.get();
    }

    @Transactional
    public Optional<EmailConfirmation> getEmailConfirmationWithChecks(UUID confirmationId, int code) {
        return emailConfirmationRepository.findById(confirmationId)
            .map(confirmation -> {
                if (confirmation.getCode() == code) {
                    confirmation.setConfirmed(true);
                    return emailConfirmationRepository.save(confirmation);
                }
                return null;
            });
    }

    @Transactional
    public EmailConfirmation createEmailConfirmation(String email) {
        int confirmationCode = CONFIRMATION_CODE_MIN + random.nextInt(CONFIRMATION_CODE_MAX - CONFIRMATION_CODE_MIN + 1);
        emailService.sendConfirmationEmail(email, confirmationCode);

        return emailConfirmationRepository.findByEmail(email)
            .map(confirmation -> {
                confirmation.setCode(confirmationCode);
                confirmation.setExpirationDate(LocalDateTime.now().plusMinutes(CONFIRMATION_EMAIL_LIFETIME_MINUTES));
                confirmation.setConfirmed(false);
                return emailConfirmationRepository.save(confirmation);
            })
            .orElseGet(() -> {
                EmailConfirmation newConfirmation = new EmailConfirmation();
                newConfirmation.setEmail(email);
                newConfirmation.setCode(confirmationCode);
                newConfirmation.setExpirationDate(LocalDateTime.now().plusMinutes(CONFIRMATION_EMAIL_LIFETIME_MINUTES));
                newConfirmation.setConfirmed(false);
                return emailConfirmationRepository.save(newConfirmation);
            });
    }
}
