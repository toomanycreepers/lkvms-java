package lamart.lkvms.application.dtos;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OrganizationWithMembersDto {
    public Long id;
    public String name;
    public List<UUID> memberIds;
}
