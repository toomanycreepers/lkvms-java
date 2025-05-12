package lamart.lkvms.infrastructure.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lamart.lkvms.application.dtos.ClosedDebtDto;
import lamart.lkvms.application.dtos.CombinedPaymentDto;
import lamart.lkvms.application.dtos.InvoiceDto;
import lamart.lkvms.application.dtos.PaginatedResponseDto;
import lamart.lkvms.application.dtos.TotalDebtDto;
import lamart.lkvms.application.services.logistic.ExportService;
import lamart.lkvms.application.services.logistic.InvoiceService;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.logistic.Invoice;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.infrastructure.auth.AuthUtils;

@RestController
@RequestMapping("/api/logistics/invoice")
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    private final ExportService exportService;
    private final UserService userService;

    InvoiceController(InvoiceService invoiceService, ExportService exportService, UserService userService) {
        this.invoiceService = invoiceService;
        this.exportService = exportService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<PaginatedResponseDto<CombinedPaymentDto>> getInvoicesAndOverpayments(
            @RequestParam(required = false) String ordering,
            @PageableDefault(sort = "dateCreated", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Organization org = AuthUtils.getCurrentOrganization(userService);
        PaginatedResponseDto<CombinedPaymentDto> response = 
            invoiceService.getCombinedPayments(pageable, ordering, org);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/total-debt/")
    public ResponseEntity<TotalDebtDto> getTotalDebt() {
        Organization org = AuthUtils.getCurrentOrganization(userService);
        TotalDebtDto dto = invoiceService.getTotalDebt(org);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/closed-debt/")
    public ResponseEntity<ClosedDebtDto> getClosedDebt() {
        Organization org = AuthUtils.getCurrentOrganization(userService);
        ClosedDebtDto dto = invoiceService.getClosedDebt(org);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/{id}/")
    public ResponseEntity<InvoiceDto> getInvoiceDetail(@PathVariable Long id) {
        InvoiceDto dto = invoiceService.getInvoiceDetails(id);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/export")
    public void exportInvoices(
            HttpServletResponse response,
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) Boolean unpaidOnly) throws IOException {

        Organization payer = AuthUtils.getCurrentOrganization(userService);
        List<Invoice> invoices = invoiceService.findActualByPayer(payer);

        response.setContentType("text/csv");
        response.setHeader(
            "Content-Disposition", 
            "attachment; filename=invoices_" + LocalDate.now() + ".csv"
        );

        exportService.exportInvoices(response, invoices);
    }
}
