package lamart.lkvms.core.entities.logistic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lamart.lkvms.core.baseclasses.SoftDeleteBase;
import lamart.lkvms.core.entities.user.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "logistics_feedback")
@Getter
@Setter
public class Feedback extends SoftDeleteBase {
    @Column(length = 500)
    String text;
    Integer rating;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User author;

    @ManyToOne
    @JoinColumn(name = "related_cargo_id", nullable = false)
    Cargo relatedCargo;
}
