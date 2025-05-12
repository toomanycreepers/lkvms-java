package lamart.lkvms.application.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lamart.lkvms.core.utilities.annotations.ValidOrganization;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserProfileUpdateDto {
    @Size(min = 1, max = 255)
    public String name;
    
    @Size(min = 1, max = 255)
    public String phoneNumber;
    
    @Email
    @Size(min = 1, max = 255)
    public String email;
    
    @ValidOrganization
    public Long selectedOrganization;
}
