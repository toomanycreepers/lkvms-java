package lamart.lkvms.application.mappers;

import java.util.List;

import lamart.lkvms.application.dtos.SessionDto;
import lamart.lkvms.core.entities.user.Session;

public class SessionMapper {

    private SessionMapper(){}

    public static List<SessionDto> convertToDtoList(List<Session> sessions){
        return sessions.stream().map(x -> new SessionDto(
            x.getId(), 
            x.getLocation(), 
            x.getDevice(), 
            x.getBrowser(), 
            x.getLastIp(), 
            x.getUpdatedAt(), 
            x.getCreatedAt()
            )).toList();
    }
}
