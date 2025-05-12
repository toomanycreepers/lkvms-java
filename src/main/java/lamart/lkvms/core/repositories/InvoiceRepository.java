package lamart.lkvms.core.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lamart.lkvms.core.entities.logistic.Invoice;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;

public interface InvoiceRepository extends SoftDeletesRepository<Invoice, Long>{

    @Query("SELECT i FROM Invoice i " +
       "LEFT JOIN FETCH i.cargos " +
       "LEFT JOIN FETCH i.invoiceDocuments " +
       "LEFT JOIN FETCH i.payer " +  
       "WHERE i.payer = :payer AND i.paid < i.amount AND i.deleted = false")
    List<Invoice> findAllByPayerAndPaidLessThanAmount(@Param("payer") Organization payer);
    
    @Query("""
        SELECT COALESCE(SUM(i.amount - i.paid), 0) 
        FROM Invoice i 
        WHERE i.payer.id = :orgId
        AND i.deleted = false""")
    BigDecimal calculateTotalDebt(@Param("orgId") Long orgId);

    @Query("""
        SELECT COALESCE(SUM(i.amount - i.paid), 0)
        FROM Invoice i 
        JOIN i.cargos c 
        WHERE i.payer.id = :orgId 
        AND c.status = :status
        AND i.deleted = false
        AND c.deleted = false""")
    BigDecimal calculateClosedDebt(@Param("orgId") Long ordId, @Param("status") CargoStatus status);
}
