package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ContractDocumentDto {
    public Long id;
    public String number;
    public LocalDateTime dateCreated;
    public String file;
}
