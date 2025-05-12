package lamart.lkvms.core.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.Session;
import lamart.lkvms.core.entities.user.User;

public interface SessionRepository extends JpaRepository<Session, UUID>{
    Optional<Session> findByUserAndBrowserAndDevice(User user, String browser, String device);
    List<Session> findByUserOrderByUpdatedAtDesc(User user);
}
