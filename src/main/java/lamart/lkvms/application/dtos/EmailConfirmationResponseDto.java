package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;

import lamart.lkvms.core.entities.user.EmailConfirmation;

public class EmailConfirmationResponseDto {
    public String message;
    public String confirmationCode;
    public LocalDateTime expiresAt;
    
    public EmailConfirmationResponseDto(String errorMessage) {
        this.message = errorMessage;
    }
    
    public EmailConfirmationResponseDto(EmailConfirmation confirmation) {
        this.message = "Confirmation sent";
        this.confirmationCode = confirmation.getCode().toString();
        this.expiresAt = confirmation.getExpirationDate();
    }
}
