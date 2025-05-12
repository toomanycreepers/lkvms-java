package lamart.lkvms.application.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto {
    public Long id;
    public String name;
    public String number;
    public Double amount;
    public Double paid;
    public String currency;
    public LocalDate billingDate;
    public LocalDate paymentDate;
    public SkinnyOrganizationDto payer;
    public List<SkinnyCargoDto> cargos;
    public List<SkinnyInvoiceDocumentDto> invoiceDocuments;
    public LocalDate dateCreated;
}
