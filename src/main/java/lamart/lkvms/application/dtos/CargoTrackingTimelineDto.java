package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;

import lamart.lkvms.core.entities.logistic.Cargo;

public record CargoTrackingTimelineDto(
    LocalDateTime statusChangedTime,
    String dislocation,
    LocalDateTime loadingDate,
    LocalDateTime etdDate,
    LocalDateTime etaDate,
    LocalDateTime billOfLandingToBrokerDate,
    LocalDateTime customsDeclarationSubmissionDate,
    LocalDateTime customsReleaseDate,
    LocalDateTime warehouseClosingDate,
    LocalDateTime ktkRemovalFromPortDate,
    LocalDateTime readyForRailroadDate,
    LocalDateTime loadedOntoRailroadDate,
    LocalDateTime railroadStationArrivalDate,
    LocalDateTime warehouseArrivalDate,
    LocalDateTime transportationEndedDate
) {
    public static CargoTrackingTimelineDto fromEntity(Cargo cargo) {
        return new CargoTrackingTimelineDto(
            cargo.getStatusChangedTime(),
            cargo.getDislocation(),
            cargo.getLoadingDate(),
            cargo.getEtdDate(),
            cargo.getEtaDate(),
            cargo.getBillOfLandingToBrokerDate(),
            cargo.getCustomsDeclarationSubmissionDate(),
            cargo.getCustomsReleaseDate(),
            cargo.getWarehouseClosingDate(),
            cargo.getKtkRemovalFromPortDate(),
            cargo.getReadyForRailroadDate(),
            cargo.getLoadedOntoRailroadDate(),
            cargo.getRailroadStationArrivalDate(),
            cargo.getWarehouseArrivalDate(),
            cargo.getTransportationEndedDate()
        );
    }
}
