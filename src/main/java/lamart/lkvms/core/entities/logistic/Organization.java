package lamart.lkvms.core.entities.logistic;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lamart.lkvms.core.baseclasses.TextEntityBase;
import lamart.lkvms.core.entities.user.User;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "logistics_organization", indexes = {
    @Index(name = "idx_org_name", columnList = "name"),
    @Index(name = "idx_org_1c", columnList = "ref_1c")
})
public class Organization extends TextEntityBase{
    
    String name;

    @ManyToMany
    @JoinTable(
        name = "logistics_organization_members",
        joinColumns = @JoinColumn(name = "organization_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> members = new HashSet<>();

    @Override
    public String toString() {
        return this.name;
    }
}
