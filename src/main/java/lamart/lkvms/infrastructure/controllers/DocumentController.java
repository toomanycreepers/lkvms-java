package lamart.lkvms.infrastructure.controllers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lamart.lkvms.application.dtos.ContractDocumentDto;
import lamart.lkvms.application.dtos.InvoiceDocumentDto;
import lamart.lkvms.application.dtos.LogisticDocumentDto;
import lamart.lkvms.application.mappers.ContractDocumentMapper;
import lamart.lkvms.application.mappers.InvoiceDocumentMapper;
import lamart.lkvms.application.mappers.LogisticDocumentMapper;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.logistic.ContractDocument;
import lamart.lkvms.core.entities.logistic.InvoiceDocument;
import lamart.lkvms.core.entities.logistic.LogisticDocument;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.repositories.ContractDoucmentRepository;
import lamart.lkvms.core.repositories.InvoiceDocumentRepository;
import lamart.lkvms.core.repositories.LogisticDocumentRepository;
import lamart.lkvms.core.specifications.ContractDocumentSpecifications;
import lamart.lkvms.core.specifications.InvoiceDocumentSpecifications;
import lamart.lkvms.core.specifications.LogisticDocumentSpecifications;
import lamart.lkvms.infrastructure.auth.AuthUtils;

@RestController
@RequestMapping("/api/logistics/document")
public class DocumentController {

    private final InvoiceDocumentRepository invoiceDocumentRepo;
    private final LogisticDocumentRepository logisticDocumentRepo;
    private final ContractDoucmentRepository contractDoucmentRepo;
    private final UserService userService;

    DocumentController(InvoiceDocumentRepository invoiceDocumentRepo, LogisticDocumentRepository logisticDocumentRepo, ContractDoucmentRepository contractDoucmentRepo, UserService userService) {
        this.invoiceDocumentRepo = invoiceDocumentRepo;
        this.logisticDocumentRepo = logisticDocumentRepo;
        this.contractDoucmentRepo = contractDoucmentRepo;
        this.userService = userService;
    }
    
    @GetMapping("/invoice/")
    public List<InvoiceDocumentDto> getInvoiceDocuments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "-dateCreated") String ordering,
            @RequestParam(required = false) LocalDate begin_date,
            @RequestParam(required = false) LocalDate end_date){
        
        Organization organization = AuthUtils.getCurrentOrganization(userService);
        if (organization == null) {
            return Collections.emptyList();
        }

        Specification<InvoiceDocument> spec = Specification.where(
            InvoiceDocumentSpecifications.forOrganization(organization))
            .and(InvoiceDocumentSpecifications.latestDocuments())
            .and(InvoiceDocumentSpecifications.withSearch(search))
            .and(InvoiceDocumentSpecifications.withType(type))
            .and(InvoiceDocumentSpecifications.withDateRange(begin_date, end_date));

        Sort sortOrder = ordering.startsWith("-") 
            ? Sort.by(ordering.substring(1)).descending()
            : Sort.by(ordering).ascending();

        return invoiceDocumentRepo.findAll(spec, sortOrder)
            .stream()
            .map(InvoiceDocumentMapper::toDto)
            .distinct()
            .toList();
    }
    
    @GetMapping("/logistic/")
    public List<LogisticDocumentDto> getLogisticDocuments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "-dateCreated") String ordering,
            @RequestParam(required = false) LocalDate begin_date,
            @RequestParam(required = false) LocalDate end_date) {

        Organization organization = AuthUtils.getCurrentOrganization(userService);
        if (organization == null) {
            return Collections.emptyList();
        }

        Specification<LogisticDocument> spec = Specification.where(
                LogisticDocumentSpecifications.forOrganization(organization))
            .and(LogisticDocumentSpecifications.withSearch(search))
            .and(LogisticDocumentSpecifications.withType(type))
            .and(LogisticDocumentSpecifications.withDateRange(begin_date, end_date));

        Sort sortOrder = ordering.startsWith("-") 
            ? Sort.by(ordering.substring(1)).descending()
            : Sort.by(ordering).ascending();

        return logisticDocumentRepo.findAll(spec, sortOrder)
            .stream()
            .map(LogisticDocumentMapper::toDto)
            .distinct()
            .toList();
    }
    
    @GetMapping("/contract/")
    public List<ContractDocumentDto> getContractDocuments(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "-dateCreated") String ordering,
            @RequestParam(required = false) LocalDate begin_date,
            @RequestParam(required = false) LocalDate end_date) {

        Organization organization = AuthUtils.getCurrentOrganization(userService);
        
        if (organization == null) {
            return Collections.emptyList();
        }

        Specification<ContractDocument> spec = Specification.where(
            ContractDocumentSpecifications.forOrganization(organization))
            .and(ContractDocumentSpecifications.withSearch(search))
            .and(ContractDocumentSpecifications.withDateRange(begin_date, end_date));

        Sort sortOrder = ordering.startsWith("-") 
            ? Sort.by(ordering.substring(1)).descending()
            : Sort.by(ordering).ascending();

        return contractDoucmentRepo.findAll(spec, sortOrder)
            .stream()
            .map(ContractDocumentMapper::toDto)
            .distinct()
            .toList();
    }
}
