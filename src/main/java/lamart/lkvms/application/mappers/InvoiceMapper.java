package lamart.lkvms.application.mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import lamart.lkvms.application.dtos.InvoiceDto;
import lamart.lkvms.application.dtos.SkinnyInvoiceDto;
import lamart.lkvms.core.entities.logistic.Invoice;

public class InvoiceMapper {
    private InvoiceMapper() {}

    public static SkinnyInvoiceDto toSkinnyDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        return new SkinnyInvoiceDto(
            invoice.getId(),
            invoice.getNumber()
        );
    }

    public static InvoiceDto toDto(Invoice invoice) {
        if (invoice == null) {
            return null;
        }

        return new InvoiceDto(
            invoice.getId(),
            invoice.getName(),
            invoice.getNumber(),
            Optional.ofNullable(invoice.getAmount())
                .map(BigDecimal::doubleValue)
                .orElse(0.0),
            Optional.ofNullable(invoice.getPaid())
                .map(BigDecimal::doubleValue)
                .orElse(0.0),
            invoice.getCurrency(),
            convertToLocalDate(invoice.getBillingDate()),
            convertToLocalDate(invoice.getPaymentDate()),
            OrganizationMapper.toDto(invoice.getPayer()),
            Optional.ofNullable(invoice.getCargos())
                .map(CargoMapper::toDtoList)
                .orElse(Collections.emptyList()),
            Optional.ofNullable(invoice.getInvoiceDocuments())
                .map(InvoiceDocumentMapper::toDtoList)
                .orElse(Collections.emptyList()),
            convertToLocalDate(invoice.getDateCreated())
        );
    }

    private static LocalDate convertToLocalDate(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
            .map(LocalDateTime::toLocalDate)
            .orElse(null);
    }
}
