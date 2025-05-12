package lamart.lkvms.core.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lamart.lkvms.core.entities.logistic.Organization;

public interface OrganizationRepository extends SoftDeletesRepository<Organization, Long> {
    @Query("SELECT o FROM Organization o JOIN o.members u WHERE u.id = :userId AND o.deleted = false")
    public List<Organization> findAllOrganizationsByUserIdAndNotDeleted(@Param("userId") UUID userId);
}
