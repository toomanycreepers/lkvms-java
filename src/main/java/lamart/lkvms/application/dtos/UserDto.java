package lamart.lkvms.application.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserDto {
    public UUID id;
    public String username;
    public String email;
    public SkinnyOrganizationDto selectedOrganization;
}
