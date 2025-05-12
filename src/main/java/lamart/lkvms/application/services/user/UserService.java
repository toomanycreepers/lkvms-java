package lamart.lkvms.application.services.user;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lamart.lkvms.core.entities.user.RegistrationInvite;
import lamart.lkvms.core.entities.user.Role;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.RoleRepository;
import lamart.lkvms.core.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final RegistrationInviteService inviteService;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final CodeService codeService;
    private final WebClient webClient;

    UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService, RegistrationInviteService inviteService, EmailService emailService, RoleRepository roleRepository, CodeService codeService, WebClient webClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.inviteService = inviteService;
        this.emailService = emailService;
        this.roleRepository = roleRepository;
        this.codeService = codeService;
        this.webClient = webClient;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        return userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        
    }

    @Transactional
    public User createUser(String username, String email, String password, String name) {
        if (username == null) {
            throw new IllegalArgumentException("Users must have a username.");
        }
        if (email == null) {
            throw new IllegalArgumentException("Users must have an email address.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email.toLowerCase());
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        
        User savedUser = userRepository.save(user);
        
        notificationService.createUserDefaultNotificationSettings(savedUser.getId());
        RegistrationInvite invite = inviteService.createInvite(savedUser);
        emailService.sendInviteEmail(savedUser, invite.getId());
        
        return savedUser;
    }

    @Transactional
    public User createSuperuser(String username, String email, String password) {
        if (password == null) {
            throw new IllegalArgumentException("Superusers must have a password.");
        }

        User user = createUser(username, email, password, null);
        user.setStaff(true);
        user.setActive(true);
        user.getRoles().add(getSuperuserRole());
        
        return userRepository.save(user);
    }

    @Transactional
    public void handleUserRegistration(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setRegistered(true);
            userRepository.save(user);
            inviteService.deleteByUser(user);
        });
    }

    public boolean isUserLinkedToTelegramBot(String username){
        User user = userRepository.findByEmail(username).orElseThrow(
            () -> new EntityNotFoundException("Username not found")
        );
        return user.getTelegramChatId() != null;
    }

    public void updateLastLoginIp(String username, String ip){
        User user = userRepository.findByEmail(username).orElseThrow(
            () -> new EntityNotFoundException("No user with this username")
        );
        user.setLastLoginIpAddress(ip);
        userRepository.save(user);
    }

    public boolean linkUserWithTelegramBotChat(String username, Long chatId){
        User user = userRepository.findByEmail(username).orElseThrow(
            () -> new EntityNotFoundException("Username not found")
        );
        user.setTelegramChatId(chatId);
        userRepository.save(user);

        codeService.deleteUsersTelegramCode(username);
        return true;
    }

    public boolean unlinkUserFromTelegramBotChat(Long chatId){
        try{
            User user = userRepository.findByTelegramChatId(chatId).orElseThrow(
                () -> new EntityNotFoundException("No user with this chat id")
            );
            user.setTelegramChatId(null);
            userRepository.save(user);
            return true;
        } catch(EntityNotFoundException e){
            return false;
        }
    }

    public void stopTelegramChat(Long chatId) {
        try {
            webClient.post()
                .bodyValue(Map.of(
                    "action", "stop",
                    "chat_id", chatId
                ))
                .retrieve()
                .toBodilessEntity()
                .block();
            
        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to stop Telegram chat: ", e);
        }
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(
            () -> new EntityNotFoundException("No user with this email.")
        );
    }

    @Transactional
    public User updateOrCreateUserWithEmailUsername(
        String email, 
        String password, 
        String name, 
        boolean isRegistered
    ) {
        String normalizedEmail = email.toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseGet(() -> {
                User newuser = new User();
                newuser.setEmail(normalizedEmail);
                return newuser;
            });

        user.setUsername(normalizedEmail);
        user.setName(name);
        user.setRegistered(isRegistered);
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    public User updateUser(User user){
        return userRepository.save(user);
    }

    @Transactional
    public void normalizeAllEmails() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            if (user.getUsername().equalsIgnoreCase(user.getEmail())) {
                user.setUsername(user.getEmail().toLowerCase());
            }
            user.setEmail(user.getEmail().toLowerCase());
        });
        userRepository.saveAll(users);
    }

    public Role getSuperuserRole() {
        return roleRepository.findByName("Admin").orElseThrow(
            () -> new EntityNotFoundException("Role with name Admin does not exist")
        );
    }   
}
