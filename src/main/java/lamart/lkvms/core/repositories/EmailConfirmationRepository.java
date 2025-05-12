package lamart.lkvms.core.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.EmailConfirmation;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, UUID> {
    Optional<EmailConfirmation> findByEmail(String email);
}
