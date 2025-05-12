package lamart.lkvms.application.services.user;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lamart.lkvms.core.entities.user.RegistrationInvite;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.RegistrationInviteRepository;

@Service
public class RegistrationInviteService {
    final RegistrationInviteRepository repo;

    RegistrationInviteService(RegistrationInviteRepository repo) {
        this.repo = repo;
    }

    public RegistrationInvite createInvite(User user){
        RegistrationInvite invite = new RegistrationInvite();
        invite.setUser(user);
        return repo.save(invite);
    }

    public void deleteByUser(User user){
        RegistrationInvite invite = repo.findByUser(user).orElseThrow(
            () -> new EntityNotFoundException("No registration invite for this user")
        );
        repo.delete(invite);
    }
}
