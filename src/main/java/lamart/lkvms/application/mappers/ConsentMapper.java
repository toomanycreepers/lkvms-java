package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.ConsentDto;
import lamart.lkvms.core.entities.user.Consent;

public class ConsentMapper {
    private ConsentMapper(){}

    public static ConsentDto toDto(Consent consent) {
        ConsentDto dto = new ConsentDto();
        dto.id = consent.getId();
        dto.updatedAt = consent.getUpdatedAt();
        dto.user = UserMapper.toUserDto(consent.getUser());
        dto.doc = DocumentMapper.toDocumentDto(consent.getDoc());
        return dto;
    }
}
