package lamart.lkvms.application.dtos;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfirmEmailRequestDto {
    public UUID emailConfirmationId;
    
    @NotBlank
    @Size(min = 6, max = 6)
    public String code;
}
