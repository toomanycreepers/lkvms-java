package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.ContractDocumentDto;
import lamart.lkvms.core.entities.logistic.ContractDocument;

public class ContractDocumentMapper {
    private ContractDocumentMapper(){}

    public static ContractDocumentDto toDto(ContractDocument doc){
        return new ContractDocumentDto(
            doc.getId(),
            doc.getNumber(),
            doc.getDateCreated(),
            doc.getFilename()
            );
    }
}
