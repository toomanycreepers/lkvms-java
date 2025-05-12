package lamart.lkvms.core.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.entities.logistic.Overpayment;

public interface OverpaymentRepository extends SoftDeletesRepository<Overpayment, Long>{
    List<Overpayment> findAllByPayer(Organization payer);
    List<Overpayment> findAllByPayerAndIsCorrectorFalse(Organization payer);
    
    @Query("SELECT COALESCE(SUM(i.paid), 0) FROM Overpayment i WHERE i.ref1c = :ref1c AND i.isCorrector = true AND i.deleted = false")
    BigDecimal sumPaidByRef1cAndIsCorrectorTrue(@Param("ref1c") String ref1c);
    
    
    @Query("SELECT COALESCE(SUM(o.paid), 0) FROM Overpayment o " +
           "WHERE o.ref1c = :ref AND o.isCorrector = true AND o.deleted = false")
    double sumCorrectionsByRef1c(String ref);

    @Query("""
        SELECT COALESCE(SUM(
            CASE WHEN o.isCorrector = false THEN o.paid ELSE -o.paid END
        ), 0) 
        FROM Overpayment o 
        WHERE o.payer.id = :orgId
        AND o.deleted = false""")
    BigDecimal calculateAvailableOverpayments(@Param("orgId") Long orgId);
}
