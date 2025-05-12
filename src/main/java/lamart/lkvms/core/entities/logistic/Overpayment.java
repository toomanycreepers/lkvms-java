package lamart.lkvms.core.entities.logistic;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lamart.lkvms.core.baseclasses.TextEntityBase;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_overpayment", indexes = {
    @Index(name = "idx_op_1c", columnList = "ref_1c")
})
@Getter
@Setter
public class Overpayment extends TextEntityBase{
    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    Organization payer;

    @Column(nullable = false)
    BigDecimal paid;

    @Column(nullable = false, length = 10)
    String currency;

    @Column(nullable = false)
    Boolean isCorrector;

    public String getName(){
        String ruName = Boolean.TRUE.equals(this.isCorrector) ? "Корректировка" : "Оплата";
        String formattedDate = this.getDateUpdated().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        return String.format("%s от %s", ruName, formattedDate);
    }
}
