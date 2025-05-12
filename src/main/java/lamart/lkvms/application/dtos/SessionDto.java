package lamart.lkvms.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SessionDto {
    public UUID id;
    public String location;
    public String device;
    public String browser;
    public String lastIp;
    public LocalDateTime updatedAt;
    public LocalDateTime createdAt;
}
