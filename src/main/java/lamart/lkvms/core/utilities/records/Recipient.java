package lamart.lkvms.core.utilities.records;

import java.util.UUID;

public record Recipient(UUID userId, String email, Long chatId) {}
