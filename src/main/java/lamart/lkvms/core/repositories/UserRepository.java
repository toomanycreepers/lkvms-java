package lamart.lkvms.core.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.User;

public interface UserRepository extends JpaRepository<User, UUID>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByTelegramChatId(Long chatId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByEmailIsNotNullAndIsStaffFalse();
}
