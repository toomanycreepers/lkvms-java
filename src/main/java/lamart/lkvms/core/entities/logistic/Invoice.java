package lamart.lkvms.core.entities.logistic;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lamart.lkvms.core.baseclasses.TextEntityBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_invoice", indexes = {
    @Index(name = "idx_invoice_1c", columnList = "ref_1c"),
    @Index(name = "idx_invoice_name", columnList = "name"),
    @Index(name = "idx_invoice_number", columnList = "number")
})
@Getter
@Setter
public class Invoice extends TextEntityBase{
    @Column(length = 255, nullable = false)
    String name;

    @Column(length = 255, nullable = false)
    String number;

    @Column(precision = 14, scale = 2, nullable = false)
    BigDecimal amount;

    @Column(precision = 14, scale = 2, nullable = false)
    BigDecimal paid;

    @Column(length = 255, nullable = true)
    String currency;

    @Column(nullable = false)
    LocalDateTime billingDate;

    @Column(nullable = true)
    LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    Organization payer;

    @ManyToMany
    @JoinTable(
        name = "logistics_invoice_cargos",
        joinColumns = @JoinColumn(name = "invoice_id"),
        inverseJoinColumns = @JoinColumn(name = "cargo_id")
    )
    Set<Cargo> cargos; 

    @OneToMany(mappedBy = "invoice")
    Set<InvoiceDocument> invoiceDocuments;

    @Override
    public String toString() {
        return this.name;
    }
}
