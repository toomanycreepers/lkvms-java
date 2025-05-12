package lamart.lkvms.core.entities.notification;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.utilities.enumerables.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "notification_settings", indexes = {
    @Index(name = "idx_notification_settings_user", columnList = "user_id"),
    @Index(name = "idx_notification_settings_cargo", columnList = "cargo_id")
})
@Getter
@Setter
public class NotificationSettings {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "etd_date", nullable = false)
    private boolean etdDate = false;

    @Column(name = "eta_date", nullable = false)
    private boolean etaDate = false;

    @Column(name = "arrived_in_port_date", nullable = false)
    private boolean arrivedInPortDate = false;

    @Column(name = "customs_release_date", nullable = false)
    private boolean customsReleaseDate = false;

    @Column(name = "loaded_onto_railroad_date", nullable = false)
    private boolean loadedOntoRailroadDate = false;

    @Column(nullable = false)
    private boolean dislocation = false;

    @Column(name = "railroad_station_arrival_date", nullable = false)
    private boolean railroadStationArrivalDate = false;

    @Column(name = "warehouse_arrival_date", nullable = false)
    private boolean warehouseArrivalDate = false;

    @Enumerated(EnumType.STRING)
    @Column(length = 2, nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;

    @Override
    public String toString() {
        if (cargo == null) {
            return String.format("Default notification settings for user %s (%s)", 
                    user.getEmail(), type.getDisplayName());
        }
        return String.format("Notification settings for user %s for cargo %s (%s)", 
                user.getEmail(), cargo.getNumber(), type.getDisplayName());
    }
}