package lamart.lkvms.core.entities.logistic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lamart.lkvms.core.baseclasses.AbstractDocument;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_contractdocument")
@Getter
@Setter
public class ContractDocument extends AbstractDocument{
    @Column(name = "contract_number", length = 510, nullable = false)
    private String number;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;
}
