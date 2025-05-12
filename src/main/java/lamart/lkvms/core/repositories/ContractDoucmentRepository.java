package lamart.lkvms.core.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lamart.lkvms.core.entities.logistic.ContractDocument;

public interface ContractDoucmentRepository extends 
    SoftDeletesRepository<ContractDocument, Long>,
    JpaSpecificationExecutor<ContractDocument> {

}
