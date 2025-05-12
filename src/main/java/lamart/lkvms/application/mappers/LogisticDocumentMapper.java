package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.LogisticDocumentDto;
import lamart.lkvms.core.entities.logistic.LogisticDocument;

public class LogisticDocumentMapper {
    private LogisticDocumentMapper(){}

    public static LogisticDocumentDto toDto(LogisticDocument doc){
        return new LogisticDocumentDto(
            doc.getId(),
            doc.getType(),
            doc.getFilename(),
            doc.getDateCreated(),
            CargoMapper.toDto(doc.getCargo())
            );
    }
}
