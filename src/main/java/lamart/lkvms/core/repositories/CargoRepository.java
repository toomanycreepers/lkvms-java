package lamart.lkvms.core.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lamart.lkvms.core.entities.logistic.Cargo;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;

public interface CargoRepository extends 
    SoftDeletesRepository<Cargo, Long>,
    JpaSpecificationExecutor<Cargo>{

    @Query("SELECT e FROM Cargo e WHERE e.deleted = false AND e.receiver = :receiver")
    List<Cargo> findAllByReceiver(Organization receiver);
    @Query("SELECT COUNT(e) FROM Cargo e WHERE e.deleted = false AND e.receiver = :receiver")
    long countActiveByReceiver(Organization receiver);
    @Query("SELECT COUNT(e) FROM Cargo e WHERE e.deleted = false AND e.receiver = :receiver AND e.status = :status")
    long countActiveByReceiverAndStatus(Organization receiver, CargoStatus status);
    Optional<Cargo>findTopByReceiverIdAndManagerFullNameIsNotNullAndManagerPhoneNumberIsNotNullOrderByDateUpdatedDesc(Long id);

    @Query("SELECT c FROM Cargo c WHERE c.receiver = :receiver " +
           "AND c.deleted = false " +
           "ORDER BY c.status ASC, c.dateUpdated DESC LIMIT 10")
    List<Cargo> findTop10ByReceiverOrderByStatusAscDateUpdatedDesc(
        @Param("receiver") Organization receiver);

    @Query("""
    SELECT 
        c,
        COALESCE(SUM(i.amount), 0) as totalAmount,
        COALESCE(SUM(i.paid), 0) as totalPaid
    FROM Cargo c
    LEFT JOIN c.invoices i
    WHERE (:org IS NULL OR c.receiver = :org)
      AND (
          :status IS NULL 
          OR c.status IN :status
      )
      AND (
          :search IS NULL OR 
          LOWER(c.number) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.waybillNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.transshipmentPort) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.billOfLanding) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.consignmentNote) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.sender) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.receiver.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.importer) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.clientNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.carrying) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.route) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.departurePoint) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.destinationPoint) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.line) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.dislocation) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.comment) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.managerFullName) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.managerPhoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
          LOWER(c.managerEmail) LIKE LOWER(CONCAT('%', :search, '%'))
      )
      AND (
          c.deleted = false
      )
    GROUP BY c
    HAVING (
        :dbu IS NULL 
        OR (c.status = '000001300' AND COALESCE(SUM(i.paid), 0) < COALESCE(SUM(i.amount), 0))
    )
""")
    Page<Object[]> findCargosWithSums(
        @Param("org") Organization org,
        @Param("status") List<CargoStatus> status,
        @Param("dbu") Boolean dbu,
        @Param("search") String search,
        Pageable pageable
    );
}
