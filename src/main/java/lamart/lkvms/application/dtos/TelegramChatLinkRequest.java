package lamart.lkvms.application.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class TelegramChatLinkRequest {
    @NotBlank
    @Max(10)
    public String userCode;

    @NotBlank
    public Long chatId;
}
