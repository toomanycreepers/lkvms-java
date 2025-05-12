package lamart.lkvms.application.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TelegramChatRequest {
        @NotBlank
        public Long chatId;
    }
