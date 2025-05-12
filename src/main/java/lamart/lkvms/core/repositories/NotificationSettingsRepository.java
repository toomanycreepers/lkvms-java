package lamart.lkvms.core.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.notification.NotificationSettings;
import lamart.lkvms.core.utilities.enumerables.NotificationType;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, UUID> {
    List<NotificationSettings> findByUserIdAndCargoId(UUID userId, Long cargoId);
    
    List<NotificationSettings> findByUserIdAndCargoIsNull(UUID userId);

    Optional<NotificationSettings> findByUserIdAndType(UUID userId, NotificationType type);
    
    void deleteByUserIdAndCargoIsNotNull(UUID userId);
    
    void deleteByUserIdAndCargoId(UUID userId, Long cargoId);
}
