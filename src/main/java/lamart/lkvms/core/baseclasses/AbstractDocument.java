package lamart.lkvms.core.baseclasses;

import java.nio.file.Paths;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractDocument extends SoftDeleteBase{
    @Column(name = "file_path", length = 255)
    private String filePath;
    
    @Transient
    public String getFilename() {
        return filePath != null ? Paths.get(filePath).getFileName().toString() : null;
    }
    
    @Override
    public String toString() {
        return getFilename() != null ? getFilename() : "No file";
    }
}
