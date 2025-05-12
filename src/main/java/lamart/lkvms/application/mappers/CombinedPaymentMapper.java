package lamart.lkvms.application.mappers;

import java.util.ArrayList;

import lamart.lkvms.application.dtos.CombinedPaymentDto;
import lamart.lkvms.application.dtos.FilepathDto;
import lamart.lkvms.core.entities.logistic.Invoice;
import lamart.lkvms.core.entities.logistic.Overpayment;

public class CombinedPaymentMapper {

    private CombinedPaymentMapper(){}

    public static CombinedPaymentDto invoiceToDto(Invoice invoice){
        return new CombinedPaymentDto(
            invoice.getId(),
            invoice.getName(),
            invoice.getNumber(),
            invoice.getAmount().doubleValue(),
            invoice.getPaid().doubleValue(),
            invoice.getCurrency(),
            invoice.getBillingDate() == null ? null : invoice.getBillingDate().toString(),
            invoice.getPaymentDate() == null ? null : invoice.getPaymentDate().toString(),
            OrganizationMapper.toDto(invoice.getPayer()),
            CargoMapper.toDtoList(invoice.getCargos()),
            invoice.getInvoiceDocuments().stream().map(x -> new FilepathDto(x.getFilePath())).toList(),
            "invoice"
        );
    }

    public static CombinedPaymentDto overpaymentToDto(Overpayment overpayment){
        return new CombinedPaymentDto(
            overpayment.getId(),
            overpayment.getName(),
            null,
            null,
            overpayment.getPaid().doubleValue(),
            overpayment.getCurrency(),
            null,
            null,
            OrganizationMapper.toDto(overpayment.getPayer()),
            new ArrayList<>(),
            new ArrayList<>(),
            "overpayment"
        );
    }

}
