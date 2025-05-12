package lamart.lkvms.application.dtos;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CurrentUserDto {
    public String name;
    public String phoneNumber;
    public String email;
    public List<SkinnyOrganizationDto> organizations;
    public SkinnyOrganizationDto selectedOrganization;
}
