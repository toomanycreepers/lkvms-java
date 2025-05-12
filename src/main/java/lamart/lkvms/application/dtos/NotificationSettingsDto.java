package lamart.lkvms.application.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingsDto {
    public UUID id;
    public boolean etdDate;
    public boolean etaDate;
    public boolean arrivedInPortDate;
    public boolean customsReleaseDate;
    public boolean loadedOntoRailroadDate;
    public boolean dislocation;
    public boolean railroadStationArrivalDate;
    public boolean warehouseArrivalDate;
    public String type; // "TG", "EM", or "DT"
    public UUID userId;
    public Long cargoId; // nullable
}
