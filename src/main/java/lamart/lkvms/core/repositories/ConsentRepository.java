package lamart.lkvms.core.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.Consent;

public interface ConsentRepository extends JpaRepository<Consent, UUID>{
    Optional<Consent> findByUserIdAndDocId(UUID userId, UUID docId);
}
