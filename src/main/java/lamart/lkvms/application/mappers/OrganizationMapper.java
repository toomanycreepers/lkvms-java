package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.OrganizationWithMembersDto;
import lamart.lkvms.application.dtos.SkinnyOrganizationDto;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.entities.user.User;

public class OrganizationMapper {
    private OrganizationMapper(){}

    public static SkinnyOrganizationDto toDto(Organization org){
        if (org == null) {
            return new SkinnyOrganizationDto(-1L, "");
        }
        return new SkinnyOrganizationDto(org.getId(), org.getName());
    }

    public static OrganizationWithMembersDto toMembersDto(Organization org){
        return new OrganizationWithMembersDto(
            org.getId(),
            org.getName(),
            org.getMembers().stream().map(User::getId).toList());
    }
}
