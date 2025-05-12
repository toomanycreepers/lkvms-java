package lamart.lkvms.application.mappers;

import java.util.Collection;
import java.util.List;

import lamart.lkvms.application.dtos.InvoiceDocumentDto;
import lamart.lkvms.application.dtos.SkinnyInvoiceDocumentDto;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.InvoiceDocument;

public class InvoiceDocumentMapper {
    private InvoiceDocumentMapper(){}

    public static SkinnyInvoiceDocumentDto toSkinnyDto(InvoiceDocument doc){
        return new SkinnyInvoiceDocumentDto(doc.getFilePath());
        //TODO check that it's not getfilename()
    }

    public static InvoiceDocumentDto toDto(InvoiceDocument doc){
        return new InvoiceDocumentDto(
            doc.getId(),
            doc.getType(),
            doc.getFilename(),
            doc.getDateCreated(),
            doc.getDateUpdated(),
            doc.getInvoice().getNumber(),
            doc.getInvoice().getCargos().stream().map(Cargo::getNumber).toList(),
            doc.getInvoice().getAmount().floatValue()
        );
    }

    public static List<SkinnyInvoiceDocumentDto> toDtoList(Collection<InvoiceDocument> docs){
        return docs.stream()
                          .map(InvoiceDocumentMapper::toSkinnyDto)
                          .toList();
    }
}
