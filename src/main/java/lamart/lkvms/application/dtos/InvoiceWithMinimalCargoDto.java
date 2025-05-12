package lamart.lkvms.application.dtos;

import lamart.lkvms.core.entities.logistic.Invoice;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InvoiceWithMinimalCargoDto {
    public Invoice invoice;
    public Long cargoId;
    public String cargoNumber;
}
