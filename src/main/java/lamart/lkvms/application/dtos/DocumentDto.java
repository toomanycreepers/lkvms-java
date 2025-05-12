package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentDto {
    public UUID id;
    public LocalDateTime uploadedAt;
    public LocalDateTime updatedAt;
    public String title;
    public byte[] content;
}
