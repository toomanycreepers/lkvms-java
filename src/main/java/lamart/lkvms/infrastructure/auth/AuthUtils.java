package lamart.lkvms.infrastructure.auth;

import org.springframework.security.core.context.SecurityContextHolder;

import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.entities.user.User;

public class AuthUtils {
    private AuthUtils(){}

    public static Organization getCurrentOrganization(UserService userService) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User userEntity = userService.getUserByEmail(user.getUsername());
        return userEntity.getSelectedOrganization();
    }
}
