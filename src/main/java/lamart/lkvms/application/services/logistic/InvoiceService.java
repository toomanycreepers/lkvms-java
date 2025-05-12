package lamart.lkvms.application.services.logistic;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lamart.lkvms.application.dtos.ClosedDebtDto;
import lamart.lkvms.application.dtos.CombinedPaymentDto;
import lamart.lkvms.application.dtos.InvoiceDto;
import lamart.lkvms.application.dtos.PaginatedResponseDto;
import lamart.lkvms.application.dtos.TotalDebtDto;
import lamart.lkvms.application.mappers.CombinedPaymentMapper;
import lamart.lkvms.application.mappers.InvoiceMapper;
import lamart.lkvms.core.entities.logistic.Invoice;
import lamart.lkvms.core.entities.logistic.Organization;
import lamart.lkvms.core.entities.logistic.Overpayment;
import lamart.lkvms.core.repositories.InvoiceRepository;
import lamart.lkvms.core.repositories.OverpaymentRepository;
import lamart.lkvms.core.utilities.enumerables.CargoStatus;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OverpaymentRepository overpaymentRepository;

    InvoiceService(OverpaymentRepository overpaymentRepository, InvoiceRepository invoiceRepository) {
        this.overpaymentRepository = overpaymentRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public PaginatedResponseDto<CombinedPaymentDto> getCombinedPayments(Pageable pageable, String ordering, Organization organization) {
        List<Invoice> invoices = invoiceRepository.findAllByPayerAndPaidLessThanAmount(organization);

        List<Overpayment> overpayments = findActiveOverpayments(organization);

        List<CombinedPaymentDto> combined = combineAndSortPayments(invoices, overpayments, ordering);

        return paginateResults(combined, pageable);
    }

    public TotalDebtDto getTotalDebt(Organization org) {
        BigDecimal debt = invoiceRepository.calculateTotalDebt(org.getId())
                          .subtract(overpaymentRepository.calculateAvailableOverpayments(org.getId()));
        
        return new TotalDebtDto(debt);
    }

    public ClosedDebtDto getClosedDebt(Organization org) {
        BigDecimal closedDebt = invoiceRepository.calculateClosedDebt(org.getId(), CargoStatus.TRANSPORTATION_COMPLETED);
        return new ClosedDebtDto(closedDebt);
    }

    public InvoiceDto getInvoiceDetails(Long pk) {
        return invoiceRepository.findById(pk)
            .map(InvoiceMapper::toDto)
            .orElseThrow(
                () -> new EntityNotFoundException("Incorrect pk")
            );
    }

    public List<Invoice> findActualByPayer(Organization payer){
        return invoiceRepository.findAllByPayerAndPaidLessThanAmount(payer);
    } 

    private List<Overpayment> findActiveOverpayments(Organization organization) {
        List<Overpayment> overpayments = overpaymentRepository.findAllByPayerAndIsCorrectorFalse(organization);
        List<Overpayment> activeOverpayments = new ArrayList<>();
        
        for (Overpayment op : overpayments) {
            BigDecimal subtractSum = overpaymentRepository.sumPaidByRef1cAndIsCorrectorTrue(op.getRef1c());
            if (subtractSum == null) subtractSum = BigDecimal.ZERO;
            
            if (subtractSum.compareTo(op.getPaid())<0) {
                op.setPaid(op.getPaid().subtract(subtractSum));
                activeOverpayments.add(op);
            }
        }
        
        return activeOverpayments;
    }

    private List<CombinedPaymentDto> combineAndSortPayments(List<Invoice> invoices, 
                                                          List<Overpayment> overpayments,
                                                          String ordering) {
        List<CombinedPaymentDto> combined = new ArrayList<>();

        invoices.forEach(invoice -> {
            CombinedPaymentDto dto = CombinedPaymentMapper.invoiceToDto(invoice);
            combined.add(dto);
        });
        
        overpayments.forEach(overpayment -> {
            CombinedPaymentDto dto = CombinedPaymentMapper.overpaymentToDto(overpayment);
            combined.add(dto);
        });

        if (ordering == null || ordering.isEmpty()) {
            ordering = "-dateCreated";
        }
        return sortCombinedData(combined, ordering);
    }

    private List<CombinedPaymentDto> sortCombinedData(List<CombinedPaymentDto> toSort, String ordering) {
        if (toSort == null || toSort.isEmpty() || ordering == null || ordering.isEmpty()) {
            return toSort;
        }

        Map<String, String> snakeTable = Map.of(
        "name", "name",
        "billing_date", "billingDate",
        "amount", "amount",
        "paid", "paid",
        "payment_date", "paymentDate",
        "date_created", "dateCreated",
        "dateCreated", "dateCreated"
        );

        String sortField = ordering.startsWith("-") ? ordering.substring(1) : ordering;
        boolean descending = ordering.startsWith("-");
        String camelSortField = snakeTable.get(sortField);

        Comparator<CombinedPaymentDto> comparator = Comparator.comparing(
            dto -> {
                try {
                    Field field = CombinedPaymentDto.class.getDeclaredField(camelSortField);
                    field.setAccessible(true);
                    Object value = field.get(dto);
                    
                    if (value == null) {
                        return null;
                    }
                    
                    if (value instanceof Number) {
                        return ((Number) value).doubleValue();
                    }

                    try {
                        return Double.parseDouble(value.toString());
                    } catch (NumberFormatException e) {
                        return value.toString();
                    }
                } catch (Exception e) {
                    return null;
                }
            },
            Comparator.nullsLast(
                Comparator.comparing(
                    obj -> {
                        if (obj instanceof Double) {
                            return (Comparable) obj;
                        } else {
                            return (Comparable) obj.toString();
                        }
                    },
                    Comparator.naturalOrder()
                )
            )
        );

        if (descending) {
            comparator = comparator.reversed();
        }

        return toSort.stream()
                .sorted(comparator)
                .toList();
    }

    private PaginatedResponseDto<CombinedPaymentDto> paginateResults(List<CombinedPaymentDto> allItems, Pageable pageable) {
        int total = allItems.size();
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        int limit = pageable.getPageSize();
        
        if (offset >= total) {
            return new PaginatedResponseDto<>(Collections.emptyList(), total, limit, offset);
        }
        
        int toIndex = Math.min(offset + limit, total);
        List<CombinedPaymentDto> paginatedList = allItems.subList(offset, toIndex);
        
        return new PaginatedResponseDto<>(paginatedList, total, limit, offset);
    }
}
