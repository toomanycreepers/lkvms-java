package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;

import lamart.lkvms.core.utilities.enumerables.LogisticDocumentType;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LogisticDocumentDto {
    public Long id;
    public LogisticDocumentType type;
    public String file;
    public LocalDateTime dateCreated;
    public SkinnyCargoDto cargo;
}
