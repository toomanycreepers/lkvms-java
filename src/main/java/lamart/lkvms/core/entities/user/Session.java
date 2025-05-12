package lamart.lkvms.core.entities.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_session")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "refresh_token", nullable = true, length = 1000)
    private String refreshToken;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String device;

    @Column(nullable = false)
    private String browser;

    @Column(name = "last_ip", length = 15, nullable = false)
    private String lastIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
