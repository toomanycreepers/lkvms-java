package lamart.lkvms.core.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.RegistrationInvite;
import lamart.lkvms.core.entities.user.User;

public interface RegistrationInviteRepository extends JpaRepository<RegistrationInvite, UUID>{
    public Optional<RegistrationInvite> findByUser(User user);
}
