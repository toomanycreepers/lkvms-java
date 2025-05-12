package lamart.lkvms.core.utilities.common;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.utilities.annotations.ValidOrganization;

public class ValidOrganizationValidator implements ConstraintValidator<ValidOrganization, Long> {
    private final UserService userService;

    ValidOrganizationValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(Long orgId, ConstraintValidatorContext context) {
        if (orgId == null) return true;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());
        
        return user.getOrganizations().stream()
            .anyMatch(org -> org.getId().equals(orgId));
    }
}