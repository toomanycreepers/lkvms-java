package lamart.lkvms.application.services.user;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lamart.lkvms.application.dtos.NotificationSettingsDto;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.notification.NotificationSettings;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.CargoRepository;
import lamart.lkvms.core.repositories.NotificationSettingsRepository;
import lamart.lkvms.core.repositories.UserRepository;
import lamart.lkvms.core.utilities.enumerables.NotificationType;

@Service
public class NotificationService {
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserRepository userRepository;
    private final CargoRepository cargoRepository;

    NotificationService(NotificationSettingsRepository notificationSettingsRepository, UserRepository userRepository, CargoRepository cargoRepository) {
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.userRepository = userRepository;
        this.cargoRepository = cargoRepository;
    }

    @Transactional
    public List<NotificationSettings> getOrCreateNotificationSettings(
            UUID userId, 
            Long cargoId, 
            boolean createCargoSettingsIfNotExists) {
        
        List<NotificationSettings> settings = cargoId != null
            ? notificationSettingsRepository.findByUserIdAndCargoId(userId, cargoId)
            : notificationSettingsRepository.findByUserIdAndCargoIsNull(userId);

        if (cargoId != null && settings.isEmpty()) {
            List<NotificationSettings> userDefaultSettings = 
                getOrCreateNotificationSettings(userId, null, false);

            if (createCargoSettingsIfNotExists) {
                settings = userDefaultSettings.stream()
                    .map(defaultSetting -> cloneForCargo(defaultSetting, cargoId))
                    .toList();
            } else {
                settings = userDefaultSettings;
            }
        }

        return settings;
    }

    public NotificationSettings findByUserAndType(UUID userId, NotificationType type) {
        return notificationSettingsRepository.findByUserIdAndType(userId, type)
        .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public List<NotificationSettings> createUserDefaultNotificationSettings(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        for (NotificationType type : 
             NotificationType.values()) {
            
            NotificationSettings settings = new NotificationSettings();
            settings.setUser(user);
            settings.setType(type);
            notificationSettingsRepository.save(settings);
        }

        return getOrCreateNotificationSettings(userId, null, false);
    }

    @Transactional
    public void deleteAllCargoNotificationSettings(UUID userId) {
        notificationSettingsRepository.deleteByUserIdAndCargoIsNotNull(userId);
    }

    @Transactional
    public void deleteSpecificCargoNotificationSettings(UUID userId, Long cargoId) {
        notificationSettingsRepository.deleteByUserIdAndCargoId(userId, cargoId);
    }

    @Transactional
    public void updateSettingsInBulk(UUID userId, List<NotificationSettingsDto> updates, 
                                   boolean shouldCreateCargoSettings) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<NotificationSettings> existingSettings = getOrCreateNotificationSettings(
            user.getId(), 
            null,
            shouldCreateCargoSettings
        );

        if (updates == null || updates.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Can't apply empty list");
        }

        for (NotificationType type : NotificationType.values()) {
            NotificationSettingsDto update = findUpdateForType(updates, type);
            if (update == null) continue;
            
            NotificationSettings setting = findExistingSetting(existingSettings, type);
            if (setting == null) continue;
            
            applyUpdate(setting, update);
        }
    }

    @Transactional
    public void updateCargoSettings(UUID userId, Long cargoId, 
                             List<NotificationSettingsDto> settingsDtos,
                             boolean createIfMissing) {
        
        List<NotificationSettings> entities = notificationSettingsRepository.findByUserIdAndCargoId(
            userId,
            cargoId
        );


        if (entities.isEmpty() && createIfMissing) {
            entities.addAll(getOrCreateNotificationSettings(
                userId,
                cargoId,
                createIfMissing
            ));
        }

        settingsDtos.forEach(dto -> 
            entities.stream()
                .filter(e -> e.getType().name().equals(dto.type))
                .findFirst()
                .ifPresent(entity -> {
                    entity.setEtdDate(dto.etdDate);
                    entity.setEtaDate(dto.etaDate);
                    entity.setArrivedInPortDate(dto.arrivedInPortDate);
                    entity.setCustomsReleaseDate(dto.customsReleaseDate);
                    entity.setLoadedOntoRailroadDate(dto.loadedOntoRailroadDate);
                    entity.setDislocation(dto.dislocation);
                    entity.setRailroadStationArrivalDate(dto.railroadStationArrivalDate);
                    entity.setWarehouseArrivalDate(dto.warehouseArrivalDate);
                    entity.setType(NotificationType.valueOf(dto.type));
                })
        );
        
        notificationSettingsRepository.saveAll(entities);
    }
    
    private NotificationSettingsDto findUpdateForType(List<NotificationSettingsDto> updates, 
                                                     NotificationType type) {
        return updates.stream()
            .filter(dto -> dto.type != null && dto.type.equals(type.name()))
            .findFirst()
            .orElse(null);
    }
    
    private NotificationSettings findExistingSetting(List<NotificationSettings> settings, 
                                                    NotificationType type) {
        return settings.stream()
            .filter(s -> s.getType() == type)
            .findFirst()
            .orElse(null);
    }
    
    private void applyUpdate(NotificationSettings setting, NotificationSettingsDto update) {
        setting.setEtdDate(update.etdDate);
        setting.setEtaDate(update.etaDate);
        setting.setArrivedInPortDate(update.arrivedInPortDate);
        setting.setCustomsReleaseDate(update.customsReleaseDate);
        setting.setLoadedOntoRailroadDate(update.loadedOntoRailroadDate);
        setting.setDislocation(update.dislocation);
        setting.setRailroadStationArrivalDate(update.railroadStationArrivalDate);
        setting.setWarehouseArrivalDate(update.warehouseArrivalDate);
        
        notificationSettingsRepository.save(setting);
    }

    private NotificationSettings cloneForCargo(NotificationSettings original, Long cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
            .orElseThrow(() -> new EntityNotFoundException("Cargo not found"));

        NotificationSettings cloned = new NotificationSettings();
        cloned.setUser(original.getUser());
        cloned.setType(original.getType());
        cloned.setCargo(cargo);
        cloned.setEtdDate(original.isEtdDate());
        cloned.setEtaDate(original.isEtaDate());
        
        return notificationSettingsRepository.save(cloned);
    }
}
