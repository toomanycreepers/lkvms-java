package lamart.lkvms.core.entities.logistic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lamart.lkvms.core.baseclasses.AbstractDocument;
import lamart.lkvms.core.utilities.enumerables.InvoiceDocumentType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_invoicedocument")
@Getter
@Setter
public class InvoiceDocument extends AbstractDocument{
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 30)
    private InvoiceDocumentType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}
