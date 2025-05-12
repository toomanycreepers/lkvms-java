package lamart.lkvms.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResetPasswordDto {
    @NotBlank
    @Size(min = 6, max = 6, message = "Code must be exactly 6 characters")
    public String code;

    @NotBlank
    @Email(message = "Email should be valid")
    public String email;

    @NotBlank
    public String newPassword;
}
