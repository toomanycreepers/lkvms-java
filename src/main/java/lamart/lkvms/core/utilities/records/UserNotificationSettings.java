package lamart.lkvms.core.utilities.records;

import java.util.List;
import java.util.UUID;

import lamart.lkvms.core.entities.notification.NotificationSettings;

public record UserNotificationSettings(UUID userId, List<NotificationSettings> settings) {}