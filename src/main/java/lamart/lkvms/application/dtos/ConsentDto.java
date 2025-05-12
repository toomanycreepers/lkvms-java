package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConsentDto {
    public UUID id;
    public LocalDateTime updatedAt;
    public UserDto user;
    public DocumentDto doc;
}
