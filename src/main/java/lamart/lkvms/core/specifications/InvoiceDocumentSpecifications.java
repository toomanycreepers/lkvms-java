package lamart.lkvms.core.specifications;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lamart.lkvms.core.entities.logistic.InvoiceDocument;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.utilities.enumerables.InvoiceDocumentType;

public class InvoiceDocumentSpecifications {
    private InvoiceDocumentSpecifications(){}

    public static Specification<InvoiceDocument> forOrganization(Organization org) {
        return (root, query, cb) -> 
            cb.equal(root.get("invoice").get("cargos").get("receiver"), org);
    }

    public static Specification<InvoiceDocument> latestDocuments() {
        return (root, query, cb) -> {
            Subquery<LocalDateTime> subquery = query.subquery(LocalDateTime.class);
            Root<InvoiceDocument> subRoot = subquery.from(InvoiceDocument.class);
            
            subquery.select(cb.greatest(subRoot.<LocalDateTime>get("dateUpdated")))
                   .where(cb.equal(subRoot.get("invoice"), root.get("invoice")),
                          cb.equal(subRoot.get("type"), root.get("type")));
            
            return cb.equal(root.get("dateUpdated"), subquery);
        };
    }

    public static Specification<InvoiceDocument> withSearch(String search) {
    return (root, query, cb) -> {
        if (StringUtils.isEmpty(search)) {
            return null;
        }

        String likePattern = "%" + search.toLowerCase() + "%";

        Predicate numericPredicate = isNumeric(search) 
            ? cb.equal(root.get("invoice").get("amount"), new BigDecimal(search))
            : cb.conjunction();

        return cb.or(
            cb.like(cb.lower(root.get("invoice").get("number")), likePattern),
            cb.like(cb.lower(root.get("invoice").get("cargos").get("number")), likePattern),
            numericPredicate
            );
        };
    }

    private static boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Specification<InvoiceDocument> withType(String type) {
        return (root, query, cb) -> 
            type != null ? cb.equal(root.get("type"), InvoiceDocumentType.valueOf(type)) : null;
    }

    public static Specification<InvoiceDocument> withDateRange(LocalDate beginDate, LocalDate endDate) {
        return (Root<InvoiceDocument> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
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
