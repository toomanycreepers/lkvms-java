package lamart.lkvms.application.services.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.thymeleaf.TemplateEngine;

import lamart.lkvms.application.services.notification.NotificationsService;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.utilities.records.ChangedField;
import lamart.lkvms.core.utilities.records.Recipient;
import lamart.lkvms.core.utilities.records.UserNotificationSettings;

public abstract class CommonNotificationService<T> {
    
    protected final TemplateEngine templateEngine;
    protected final NotificationsService notificationsService;
    
    protected final T obj;
    protected final List<User> recipients;
    protected final Map<String, Object> initialValues;

    protected CommonNotificationService(T obj, List<User> recipients, TemplateEngine templateEngine, NotificationsService notificationsService) {
        this.obj = obj;
        this.recipients = recipients;
        this.initialValues = captureInitialValues();
        this.templateEngine = templateEngine;
        this.notificationsService = notificationsService;
    }

    protected abstract Map<String, Object> captureInitialValues();

    public abstract List<String> getFieldsToCheck();

    public abstract List<Recipient> getRecipients();

    public abstract Map<UUID, String> createEmailMessages(
        List<UserNotificationSettings> settings,
        List<ChangedField> changedFields
    );

    public abstract String createEmailSubject();

    public abstract List<UserNotificationSettings> getNotificationSettings();

    public abstract Map<UUID, String> createTelegramMessages(
        List<UserNotificationSettings> settings,
        List<ChangedField> changedFields
    );

    public List<ChangedField> parseObject() {
        List<ChangedField> changedFields = new ArrayList<>();
        
        try {
            for (String field : getFieldsToCheck()) {
                Object currentValue = getFieldValue(obj, field);
                Object initialValue = initialValues.get(field);
                
                if (!Objects.equals(initialValue, currentValue)) {
                    changedFields.add(new ChangedField(
                        field,
                        initialValue,
                        currentValue
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error detecting changed fields", e);
        }
        
        return changedFields;
    }

    public void sendNotifications() {
        List<Recipient> recipientsToNotify = getRecipients();
        List<UserNotificationSettings> notificationSettings = getNotificationSettings();
        List<ChangedField> changedFields = parseObject();
        
        if (changedFields.isEmpty()) {
            return;
        }
        
        Map<UUID, String> emailMessages = createEmailMessages(notificationSettings, changedFields);
        Map<UUID, String> telegramMessages = createTelegramMessages(notificationSettings, changedFields);
        
        for (Recipient recipient : recipientsToNotify) {
            String emailMessage = emailMessages.get(recipient.userId());
            String telegramMessage = telegramMessages.get(recipient.userId());
            
            if (emailMessage != null && recipient.email() != null) {
                notificationsService.sendNotificationEmail(
                    recipient.email(),
                    createEmailSubject(),
                    emailMessage
                );
            }
            
            if (telegramMessage != null && recipient.chatId() != null) {
                notificationsService.sendNotificationTelegram(
                    recipient.chatId(),
                    telegramMessage
                );
            }
        }
    }

    protected Object getFieldValue(Object object, String fieldName) {
        try {
            var field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException("Failed to access field: " + fieldName, e);
        }
    }
}
