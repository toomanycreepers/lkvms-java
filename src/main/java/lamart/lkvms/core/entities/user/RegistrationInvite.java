package lamart.lkvms.core.entities.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_registrationinvite")
@Getter
@Setter
public class RegistrationInvite {
    @Id
    @GeneratedValue(generator = "UUID")
    UUID id; 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(nullable = true)
    LocalDateTime lastSentAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;
}
