package lamart.lkvms.application.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserRegisterDto {
    public String name;
    public String password;
    public String emailConfirmationId;
}
