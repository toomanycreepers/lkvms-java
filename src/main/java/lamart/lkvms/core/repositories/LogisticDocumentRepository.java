package lamart.lkvms.core.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lamart.lkvms.core.entities.logistic.LogisticDocument;

public interface LogisticDocumentRepository 
    extends SoftDeletesRepository<LogisticDocument, Long>,
    JpaSpecificationExecutor<LogisticDocument> {
}
