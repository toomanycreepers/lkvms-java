package lamart.lkvms.core.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lamart.lkvms.core.entities.logistic.InvoiceDocument;

public interface InvoiceDocumentRepository extends 
    SoftDeletesRepository<InvoiceDocument, Long>,
    JpaSpecificationExecutor<InvoiceDocument>{
}
