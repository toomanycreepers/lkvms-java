package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.UserDto;
import lamart.lkvms.core.entities.user.User;

public class UserMapper {
    private UserMapper(){}

    public static UserDto toUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getDisplayName(),
            user.getEmail(),
            OrganizationMapper.toDto(user.getSelectedOrganization())
        );
    }
}
