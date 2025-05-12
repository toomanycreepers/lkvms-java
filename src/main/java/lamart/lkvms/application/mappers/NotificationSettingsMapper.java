package lamart.lkvms.application.mappers;

import lamart.lkvms.application.dtos.NotificationSettingsDto;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.notification.NotificationSettings;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.utilities.enumerables.NotificationType;

public class NotificationSettingsMapper {
    private NotificationSettingsMapper(){}

    public static NotificationSettingsDto toDto(NotificationSettings entity) {
        NotificationSettingsDto dto = new NotificationSettingsDto();

        dto.id = entity.getId();
        dto.etdDate = entity.isEtdDate();
        dto.etaDate = entity.isEtaDate();
        dto.arrivedInPortDate = entity.isArrivedInPortDate();
        dto.customsReleaseDate = entity.isCustomsReleaseDate();
        dto.loadedOntoRailroadDate = entity.isLoadedOntoRailroadDate();
        dto.dislocation = entity.isDislocation();
        dto.railroadStationArrivalDate = entity.isRailroadStationArrivalDate();
        dto.warehouseArrivalDate = entity.isWarehouseArrivalDate();

        dto.type = entity.getType().name();

        dto.userId = entity.getUser().getId();
        if (entity.getCargo() != null) {
            dto.cargoId = entity.getCargo().getId();
        }
        
        return dto;
    }

    public static NotificationSettings toEntity(NotificationSettingsDto dto, 
                                              User user, 
                                              Cargo cargo) {
        NotificationSettings entity = new NotificationSettings();

        entity.setId(dto.id);
        entity.setEtdDate(dto.etdDate);
        entity.setEtaDate(dto.etaDate);
        entity.setArrivedInPortDate(dto.arrivedInPortDate);
        entity.setCustomsReleaseDate(dto.customsReleaseDate);
        entity.setLoadedOntoRailroadDate(dto.loadedOntoRailroadDate);
        entity.setDislocation(dto.dislocation);
        entity.setRailroadStationArrivalDate(dto.railroadStationArrivalDate);
        entity.setWarehouseArrivalDate(dto.warehouseArrivalDate);

        entity.setType(NotificationType.valueOf(dto.type));

        entity.setUser(user);
        entity.setCargo(cargo);
        
        return entity;
    }
}
