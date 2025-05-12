package lamart.lkvms.core.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lamart.lkvms.core.entities.logistic.LogisticDocument;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.utilities.enumerables.LogisticDocumentType;

public class LogisticDocumentSpecifications {
    private LogisticDocumentSpecifications(){}

    public static Specification<LogisticDocument> forOrganization(Organization org) {
        return (root, query, cb) -> 
            cb.equal(root.get("cargo").get("receiver"), org);
    }

    public static Specification<LogisticDocument> withSearch(String search) {
        return (root, query, cb) -> {
            if (search == null) return null;
            return cb.or(
                cb.like(root.get("number"), "%" + search + "%"),
                cb.like(root.get("cargo").get("number"), "%" + search + "%")
            );
        };
    }

    public static Specification<LogisticDocument> withType(String type) {
        return (root, query, cb) -> 
            type != null ? cb.equal(root.get("type"), LogisticDocumentType.valueOf(type)) : null;
    }

    public static Specification<LogisticDocument> withDateRange(LocalDate beginDate, LocalDate endDate) {
        return (Root<LogisticDocument> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
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
