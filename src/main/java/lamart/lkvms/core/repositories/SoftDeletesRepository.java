package lamart.lkvms.core.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

import jakarta.transaction.Transactional;
import lamart.lkvms.core.baseclasses.SoftDeleteBase;

@NoRepositoryBean
public interface SoftDeletesRepository<T extends SoftDeleteBase, ID> extends JpaRepository<T, ID> {

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = false")
    @Override
    @NonNull
    List<T> findAll();

    @Query("SELECT e FROM #{#entityName} e WHERE e.deleted = true")
    List<T> findAllDeleted();

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1")
    Optional<T> findByIdIncludingDeleted(ID id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = true, e.deletedAt = CURRENT_TIMESTAMP WHERE e.id = ?1")
    void softDeleteById(ID id);

    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deleted = false, e.deletedAt = null WHERE e.id = ?1")
    void restoreById(ID id);

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = false")
    long countActive();

    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deleted = true")
    long countDeleted();

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = ?1 AND e.deleted = false")
    @NonNull
    Optional<T> findById(@NonNull ID id);
}
