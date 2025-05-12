package lamart.lkvms.application.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Invoice;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;
import lamart.lkvms.core.utilities.enumerables.CargoType;

public record CargoDetailDto(
    Long id,
    LocalDateTime deletedAt,
    LocalDateTime dateUpdated,
    LocalDateTime dateCreated,
    String ref1c,
    String number,
    LocalDateTime date,
    String waybillNumber,
    String billOfLanding,
    String consignmentNote,
    String sender,
    String importer,
    String clientNumber,
    CargoType type,
    String status,
    LocalDateTime statusChangedTime,
    String carrying,
    int sizeType,
    String route,
    String departurePoint,
    String destinationPoint,
    String transshipmentPort,
    String line,
    String dislocation,
    boolean isMblReleased,
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
    LocalDateTime warehouseArrivalDate,
    String comment,
    int order,
    String managerFullName,
    String managerPhoneNumber,
    String managerEmail,
    ReceiverDto receiver
) {
    public record ReceiverDto(
        Long id,
        LocalDateTime dateUpdated,
        LocalDateTime dateCreated,
        String ref1c,
        String name,
        List<UUID> members
    ) {}
    public static CargoDetailDto fromEntity(Cargo cargo) {
        return new CargoDetailDto(
            cargo.getId(),
            cargo.getDeletedAt(),
            cargo.getDateUpdated(),
            cargo.getDateCreated(),
            cargo.getRef1c(),
            cargo.getNumber(),
            cargo.getDate(),
            cargo.getWaybillNumber(),
            cargo.getBillOfLanding(),
            cargo.getConsignmentNote(),
            cargo.getSender(),
            cargo.getImporter(),
            cargo.getClientNumber(),
            cargo.getType(),
            cargo.getStatus().getCode(),
            cargo.getStatusChangedTime(),
            cargo.getCarrying(),
            cargo.getSizeType().ordinal(),
            cargo.getRoute(),
            cargo.getDeparturePoint(),
            cargo.getDestinationPoint(),
            cargo.getTransshipmentPort(),
            cargo.getLine(),
            cargo.getDislocation(),
            cargo.getIsMblReleased() != null && cargo.getIsMblReleased(),
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
            cargo.getWarehouseArrivalDate(),
            cargo.getComment(),
            cargo.getOrderNumber(),
            cargo.getManagerFullName(),
            cargo.getManagerPhoneNumber(),
            cargo.getManagerEmail(),
            cargo.getReceiver() != null ? 
                new ReceiverDto(
                    cargo.getReceiver().getId(),
                    cargo.getReceiver().getDateUpdated(),
                    cargo.getReceiver().getDateCreated(),
                    cargo.getReceiver().getRef1c(),
                    cargo.getReceiver().getName(),
                    cargo.getReceiver().getMembers().stream()
                        .map(User::getId)
                        .toList()
                ) : null
        );
    }

    private static boolean calculateIsDeliveredButUnpaid(Cargo cargo) {
        return cargo.getStatus() == CargoStatus.TRANSPORTATION_COMPLETED && 
               cargo.getInvoices().stream()
                   .anyMatch(inv -> inv.getPaid().compareTo(inv.getAmount()) < 0);
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
