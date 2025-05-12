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
import lamart.lkvms.core.utilities.enumerables.LogisticDocumentType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_logisticdocument")
@Getter
@Setter
public class LogisticDocument extends AbstractDocument{
    @Column(name = "document_number", length = 500)
    private String number;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 30)
    private LogisticDocumentType type;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    private Cargo cargo;
}
