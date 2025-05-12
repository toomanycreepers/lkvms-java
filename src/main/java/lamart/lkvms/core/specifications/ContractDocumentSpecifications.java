package lamart.lkvms.core.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lamart.lkvms.core.entities.logistic.ContractDocument;
import lamart.lkvms.core.entities.logistic.Organization;

public class ContractDocumentSpecifications {

    private ContractDocumentSpecifications(){}

    public static Specification<ContractDocument> forOrganization(Organization org) {
        return (root, query, cb) -> cb.equal(root.get("organization"), org);
    }

    public static Specification<ContractDocument> withSearch(String search) {
        return (root, query, cb) -> 
            search != null ? cb.like(root.get("number"), "%" + search + "%") : null;
    }

    public static Specification<ContractDocument> withDateRange(LocalDate beginDate, LocalDate endDate) {
        return (Root<ContractDocument> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            Predicate predicate = cb.conjunction();
            
            if (beginDate != null) {
                predicate = cb.and(predicate, 
                    cb.greaterThanOrEqualTo(root.get("dateUpdated"), beginDate));
            }
            
            if (endDate != null) {
                predicate = cb.and(predicate,
                    cb.lessThanOrEqualTo(root.get("dateUpdated"), endDate));
            }
            
            return predicate;
        };
    }
}
