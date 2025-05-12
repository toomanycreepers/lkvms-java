package lamart.lkvms.core.baseclasses;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class TextEntityBase extends SoftDeleteBase{
    @Column(name = "ref_1c", nullable = false, length = 512)
    String ref1c;
}
