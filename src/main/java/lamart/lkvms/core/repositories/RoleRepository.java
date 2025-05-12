package lamart.lkvms.core.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.Role;

public interface RoleRepository extends JpaRepository<Role, Long>{
    public Optional<Role> findByName(String roleName);
}
