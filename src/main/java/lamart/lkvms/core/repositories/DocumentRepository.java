package lamart.lkvms.core.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lamart.lkvms.core.entities.user.Document;


public interface DocumentRepository extends JpaRepository<Document, UUID>{
    Optional<Document> findByTitle(String title);
    boolean existsByTitle(String title);
}
