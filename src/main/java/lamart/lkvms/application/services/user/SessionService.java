package lamart.lkvms.application.services.user;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lamart.lkvms.core.entities.user.Session;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.SessionRepository;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final IpLocationService ipLocationService;

    SessionService(SessionRepository sessionRepository, IpLocationService ipLocationService) {
        this.sessionRepository = sessionRepository;
        this.ipLocationService = ipLocationService;
    }

    @Transactional
    public Session updateOrCreateSession(
        User user, 
        String browser, 
        String device, 
        String ip
    ) {
        String location = ipLocationService.getLocation(ip).orElse("");
        return sessionRepository.findByUserAndBrowserAndDevice(user, browser, device)
            .map(session -> {
                session.setLocation(location);
                session.setLastIp(ip);
                return sessionRepository.save(session);
            })
            .orElseGet(() -> {
                Session newSession = new Session();
                newSession.setUser(user);
                newSession.setBrowser(browser);
                newSession.setDevice(device);
                newSession.setLocation(location);
                newSession.setLastIp(ip);
                newSession.setRefreshToken("");
                return sessionRepository.save(newSession);
            });
    }

    public Session updateSessionRefreshToken(UUID sessionId, String refreshToken) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new EntityNotFoundException("Session not found"));
        session.setRefreshToken(refreshToken);
        return sessionRepository.save(session);
    }

    public List<Session> getSessions(User user) {
        return sessionRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    public Session findSessionById(UUID sessionId){
        return sessionRepository.findById(sessionId).orElseThrow(
            () -> new EntityNotFoundException("Incorrect session id")
        );
    }

    public void deleteSession(Session session){
        sessionRepository.delete(session);
    }
}
