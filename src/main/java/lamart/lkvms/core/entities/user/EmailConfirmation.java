package lamart.lkvms.core.entities.user;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_emailconfirmation")
@Getter
@Setter
public class EmailConfirmation {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false)
    private Integer code;

    @Column(length = 250, nullable = false)
    private String email;

    @Column(name = "expiration_date", nullable = false)
    private LocalDateTime expirationDate;

    @Column(nullable = false)
    private boolean confirmed = false;
}
