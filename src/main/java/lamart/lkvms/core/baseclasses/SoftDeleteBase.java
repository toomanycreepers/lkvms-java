package lamart.lkvms.core.baseclasses;

import java.time.LocalDateTime;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@SQLDelete(sql = "UPDATE #{#entityName} SET deleted = true, deleted_at = NOW() WHERE id = ?")
@FilterDef(
    name = "deletedFilter",
    parameters = @ParamDef(name = "isDeleted", type = boolean.class)
)
@Filter(
    name = "deletedFilter",
    condition = "deleted = :isDeleted"
)
public abstract class SoftDeleteBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime dateCreated;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime dateUpdated;

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }
}
