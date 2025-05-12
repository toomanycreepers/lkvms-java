package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.DocumentDto;
import lamart.lkvms.core.entities.user.Document;

public class DocumentMapper {
    private DocumentMapper(){}

    public static DocumentDto toDocumentDto(Document doc) {
        DocumentDto dto = new DocumentDto();
        dto.id = doc.getId();
        dto.title = doc.getTitle();
        dto.content = doc.getContent();
        dto.uploadedAt = doc.getUploadedAt();
        dto.updatedAt = doc.getUpdatedAt();
        return dto;
    }
}
