package lamart.lkvms.application.services.logistic;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriUtils;

import lamart.lkvms.core.baseclasses.AbstractDocument;
import lamart.lkvms.core.entities.logistic.ContractDocument;
import lamart.lkvms.core.entities.logistic.InvoiceDocument;
import lamart.lkvms.core.entities.logistic.LogisticDocument;
import lamart.lkvms.core.repositories.ContractDoucmentRepository;
import lamart.lkvms.core.repositories.InvoiceDocumentRepository;
import lamart.lkvms.core.repositories.LogisticDocumentRepository;
import lamart.lkvms.core.utilities.enumerables.InvoiceDocumentType;

@Service
public class FileService {

    final InvoiceDocumentRepository invoiceDocumentRepository;

    final LogisticDocumentRepository logisticDocumentRepository;

    final ContractDoucmentRepository contractDocumentRepository;

    FileService(InvoiceDocumentRepository invoiceDocumentRepository, LogisticDocumentRepository logisticDocumentRepository, ContractDoucmentRepository contractDocumentRepository) {
        this.invoiceDocumentRepository = invoiceDocumentRepository;
        this.logisticDocumentRepository = logisticDocumentRepository;
        this.contractDocumentRepository = contractDocumentRepository;
    }

    public String fixFilePath(String filePath) {
        if (filePath.contains("/")) {
            filePath = filePath.substring(filePath.lastIndexOf("/") + 1);
        }
        return UriUtils.decode(filePath, StandardCharsets.UTF_8);
    }

    @Transactional
    public void fixFiles() {
        fixDocumentsPaths(invoiceDocumentRepository.findAll());
        fixDocumentsPaths(logisticDocumentRepository.findAll());
        fixDocumentsPaths(contractDocumentRepository.findAll());

        List<InvoiceDocument> invoices = invoiceDocumentRepository.findAll();
        for (InvoiceDocument invoice : invoices) {
            setInvoiceDocumentType(invoice);
        }
    }

     @Transactional
    public <T extends AbstractDocument> void fixDocumentsPaths(List<T> documents) {
        for (T document : documents) {
            document.setFilePath(fixFilePath(document.getFilename()));
        }
        if (!documents.isEmpty()) {
            if (documents.get(0) instanceof InvoiceDocument) {
                invoiceDocumentRepository.saveAll((List<InvoiceDocument>) documents);
            } else if (documents.get(0) instanceof LogisticDocument) {
                logisticDocumentRepository.saveAll((List<LogisticDocument>) documents);
            } else if (documents.get(0) instanceof ContractDocument) {
                contractDocumentRepository.saveAll((List<ContractDocument>) documents);
            }
        }
    }

    @Transactional
    public void setInvoiceDocumentType(InvoiceDocument invoiceDocument) {
        String fileName = invoiceDocument.getFilename();

        if (fileName.contains("Invoice")) {
            invoiceDocument.setType(InvoiceDocumentType.INVOICE);
        } else if (fileName.contains("UPD")) {
            invoiceDocument.setType(InvoiceDocumentType.UNIVERSAL_TRANSFER_DOCUMENT);
        }

        invoiceDocumentRepository.save(invoiceDocument);
    }

}
