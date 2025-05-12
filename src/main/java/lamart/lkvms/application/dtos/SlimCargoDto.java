package lamart.lkvms.application.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Invoice;

public record SlimCargoDto(
    Long id,
    Integer order,
    String number,
    String route,
    String status,
    boolean isCargoDeliveredButHasUnpaidInvoices,
    LocalDateTime statusChangedTime,
    LocalDateTime readinessDate,
    LocalDateTime loadingDate,
    LocalDateTime etdDate,
    LocalDateTime billOfLandingToBrokerDate,
    LocalDateTime warehouseClosingDate,
    LocalDateTime ktkRemovalFromPortDate,
    LocalDateTime transportationEndedDate,
    LocalDateTime etaDate,
    LocalDateTime arrivedInPortDate,
    LocalDateTime customsDeclarationSubmissionDate,
    LocalDateTime customsReleaseDate,
    LocalDateTime readyForRailroadDate,
    LocalDateTime loadedOntoRailroadDate,
    LocalDateTime railroadStationArrivalDate,
    LocalDateTime warehouseArrivalDate

) {
    public static SlimCargoDto fromEntity(Cargo cargo) {
        return new SlimCargoDto(
            cargo.getId(),
            cargo.getOrderNumber(),
            cargo.getNumber(),
            cargo.getRoute(),
            cargo.getStatus().getCode(),
            cargo.isCargoDeliveredButHasUnpaidInvoices(),
            cargo.getStatusChangedTime(),
            cargo.getReadinessDate(),
            cargo.getLoadingDate(),
            cargo.getEtdDate(),
            cargo.getBillOfLandingToBrokerDate(),
            cargo.getWarehouseClosingDate(),
            cargo.getKtkRemovalFromPortDate(),
            cargo.getTransportationEndedDate(),
            cargo.getEtaDate(),
            cargo.getArrivedInPortDate(),
            cargo.getCustomsDeclarationSubmissionDate(),
            cargo.getCustomsReleaseDate(),
            cargo.getReadyForRailroadDate(),
            cargo.getLoadedOntoRailroadDate(),
            cargo.getRailroadStationArrivalDate(),
            cargo.getWarehouseArrivalDate()
            );
    }

    private static BigDecimal calculateTotalAmount(Set<Invoice> invoices) {
        return invoices.stream()
            .map(Invoice::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal calculateTotalPaid(Set<Invoice> invoices) {
        return invoices.stream()
            .map(Invoice::getPaid)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}