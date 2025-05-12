package lamart.lkvms.application.services.logistic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import jakarta.persistence.Column;
import jakarta.servlet.http.HttpServletResponse;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Invoice;

@Service
public class ExportService {

    public void exportCargos(HttpServletResponse response, List<Cargo> cargos) throws IOException {
        String[] headers = getExportFields(Cargo.class);
        String[] fieldNames = getFieldNames(Cargo.class);
        
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=cargos.csv");
        
        try (ICsvMapWriter csvWriter = new CsvMapWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvWriter.writeHeader(headers);
            
            for (Cargo cargo : cargos) {
                Map<String, Object> cargoMap = mapCargoForExport(cargo);
                csvWriter.write(cargoMap, fieldNames);
            }
        }
    }

    public void exportInvoices(HttpServletResponse response, List<Invoice> invoices) throws IOException {
        String[] headers = getExportFields(Invoice.class, 
            "name", "number", "amount", "paid", "currency", "billingDate", "paymentDate");
        String[] fieldNames = getFieldNames(Invoice.class, 
            "name", "number", "amount", "paid", "currency", "billingDate", "paymentDate");
        
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=invoices.csv");
        
        try (ICsvMapWriter csvWriter = new CsvMapWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE)) {
            csvWriter.writeHeader(headers);
            
            for (Invoice invoice : invoices) {
                Map<String, Object> invoiceMap = mapInvoiceForExport(invoice);
                csvWriter.write(invoiceMap, fieldNames);
            }
        }
    }

    private String[] getExportFields(Class<?> clazz, String... includedFields) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldList = includedFields.length > 0 ? 
            Arrays.asList(includedFields) : 
            Arrays.stream(fields)
                .map(Field::getName)
                .filter(name -> !Arrays.asList("deleted", "deletedAt", "ref1c", "dateUpdated", "dateCreated").contains(name))
                .toList();
        
        return fieldList.stream()
            .map(field -> {
                try {
                    Field f = clazz.getDeclaredField(field);
                    if (f.isAnnotationPresent(Column.class)) {
                        Column column = f.getAnnotation(Column.class);
                        return column.name().isEmpty() ? field : column.name();
                    }
                    return field;
                } catch (NoSuchFieldException e) {
                    return field;
                }
            })
            .toArray(String[]::new);
    }

    private String[] getFieldNames(Class<?> clazz, String... includedFields) {
        Field[] fields = clazz.getDeclaredFields();
        return includedFields.length > 0 ? 
            includedFields : 
            Arrays.stream(fields)
                .map(Field::getName)
                .filter(name -> !Arrays.asList("deleted", "deletedByCascade", "ref1c", "dateUpdated", "dateCreated").contains(name))
                .toArray(String[]::new);
    }

    private Map<String, Object> mapCargoForExport(Cargo cargo) {
    Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", cargo.getId());
        map.put("number", cargo.getNumber());
        map.put("waybillNumber", cargo.getWaybillNumber());
        map.put("billOfLanding", cargo.getBillOfLanding());
        map.put("consignmentNote", cargo.getConsignmentNote());
        map.put("sender", cargo.getSender());
        map.put("importer", cargo.getImporter());
        map.put("clientNumber", cargo.getClientNumber());
        map.put("carrying", cargo.getCarrying());
        map.put("route", cargo.getRoute());
        map.put("departurePoint", cargo.getDeparturePoint());
        map.put("destinationPoint", cargo.getDestinationPoint());
        map.put("transshipmentPort", cargo.getTransshipmentPort());
        map.put("line", cargo.getLine());
        map.put("dislocation", cargo.getDislocation());
        map.put("comment", cargo.getComment());
        map.put("orderNumber", cargo.getOrderNumber());
        map.put("managerFullName", cargo.getManagerFullName());
        map.put("managerPhoneNumber", cargo.getManagerPhoneNumber());
        map.put("managerEmail", cargo.getManagerEmail());
        map.put("ref1c", cargo.getRef1c());
        map.put("type", cargo.getType() != null ? cargo.getType().getDisplayName() : null);
        map.put("sizeType", cargo.getSizeType() != null ? cargo.getSizeType().getDisplayName() : null);
        map.put("status", cargo.getStatus() != null ? cargo.getStatus().getCode() : null);
        map.put("date", formatDate(cargo.getDate()));
        map.put("readinessDate", formatDate(cargo.getReadinessDate()));
        map.put("loadingDate", formatDate(cargo.getLoadingDate()));
        map.put("etdDate", formatDate(cargo.getEtdDate()));
        map.put("etaDate", formatDate(cargo.getEtaDate()));
        map.put("arrivedInPortDate", formatDate(cargo.getArrivedInPortDate()));
        map.put("customsDeclarationSubmissionDate", formatDate(cargo.getCustomsDeclarationSubmissionDate()));
        map.put("customsReleaseDate", formatDate(cargo.getCustomsReleaseDate()));
        map.put("readyForRailroadDate", formatDate(cargo.getReadyForRailroadDate()));
        map.put("loadedOntoRailroadDate", formatDate(cargo.getLoadedOntoRailroadDate()));
        map.put("railroadStationArrivalDate", formatDate(cargo.getRailroadStationArrivalDate()));
        map.put("warehouseArrivalDate", formatDate(cargo.getWarehouseArrivalDate()));
        map.put("billOfLandingToBrokerDate", formatDate(cargo.getBillOfLandingToBrokerDate()));
        map.put("ktkRemovalFromPortDate", formatDate(cargo.getKtkRemovalFromPortDate()));
        map.put("transportationEndedDate", formatDate(cargo.getTransportationEndedDate()));
        map.put("warehouseClosingDate", formatDate(cargo.getWarehouseClosingDate()));
        map.put("statusChangedTime", formatDate(cargo.getStatusChangedTime()));
        map.put("dateCreated", formatDate(cargo.getDateCreated()));
        map.put("dateUpdated", formatDate(cargo.getDateUpdated()));
        map.put("deletedAt", formatDate(cargo.getDeletedAt()));
        map.put("isMblReleased", cargo.getIsMblReleased());
        map.put("deleted", cargo.isDeleted());
        map.put("receiver", cargo.getReceiver() != null ? cargo.getReceiver().getId() : null);
        
        return map;
    }

    private String formatDate(LocalDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    private Map<String, Object> mapInvoiceForExport(Invoice invoice) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", invoice.getId());
        map.put("deletedAt", invoice.getDeletedAt());
        map.put("deleted", invoice.getDeletedAt() == null);
        map.put("ref1c", invoice.getRef1c());
        map.put("dateCreated", invoice.getDateCreated());
        map.put("dateUpdated", invoice.getDateUpdated());
        map.put("name", invoice.getName());
        map.put("number", invoice.getNumber());
        map.put("amount", invoice.getAmount());
        map.put("paid", invoice.getPaid());
        map.put("currency", invoice.getCurrency());
        map.put("billingDate", invoice.getBillingDate());
        map.put("paymentDate", invoice.getPaymentDate());
        
        return map;
    }
}
