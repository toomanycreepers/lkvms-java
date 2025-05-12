package lamart.lkvms.application.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PasswordChangeDto {
    public String currentPassword;
    public String newPassword;
    public String confirmPassword;
}
