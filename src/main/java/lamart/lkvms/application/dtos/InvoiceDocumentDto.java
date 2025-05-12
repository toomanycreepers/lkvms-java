package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;
import java.util.List;

import lamart.lkvms.core.utilities.enumerables.InvoiceDocumentType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvoiceDocumentDto {
    public Long id;
    public InvoiceDocumentType type;
    public String file;
    public LocalDateTime dateCreated;
    public LocalDateTime dateUpdated;
    public String number;
    public List<String> cargoNumber;
    public Float amount;
}
