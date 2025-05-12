package lamart.lkvms.infrastructure.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lamart.lkvms.application.dtos.AccessTokenResponse;
import lamart.lkvms.application.dtos.ConfirmEmailRequestDto;
import lamart.lkvms.application.dtos.ConsentDto;
import lamart.lkvms.application.dtos.CurrentUserDto;
import lamart.lkvms.application.dtos.EmailConfirmationResponseDto;
import lamart.lkvms.application.dtos.LoginRequest;
import lamart.lkvms.application.dtos.PasswordChangeDto;
import lamart.lkvms.application.dtos.PasswordResetRequestDto;
import lamart.lkvms.application.dtos.ResetPasswordDto;
import lamart.lkvms.application.dtos.SessionDto;
import lamart.lkvms.application.dtos.TelegramChatLinkRequest;
import lamart.lkvms.application.dtos.TelegramChatRequest;
import lamart.lkvms.application.dtos.TelegramLinkDto;
import lamart.lkvms.application.dtos.TokenRefreshRequest;
import lamart.lkvms.application.dtos.TokenResponse;
import lamart.lkvms.application.dtos.UserProfileUpdateDto;
import lamart.lkvms.application.dtos.UserRegisterDto;
import lamart.lkvms.application.mappers.ConsentMapper;
import lamart.lkvms.application.mappers.OrganizationMapper;
import lamart.lkvms.application.mappers.SessionMapper;
import lamart.lkvms.application.services.logistic.OrganizationService;
import lamart.lkvms.application.services.user.AuthService;
import lamart.lkvms.application.services.user.CodeService;
import lamart.lkvms.application.services.user.EmailConfirmationService;
import lamart.lkvms.application.services.user.EmailService;
import lamart.lkvms.application.services.user.SessionService;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.user.Consent;
import lamart.lkvms.core.entities.user.Document;
import lamart.lkvms.core.entities.user.EmailConfirmation;
import lamart.lkvms.core.entities.user.Session;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.ConsentRepository;
import lamart.lkvms.core.repositories.DocumentRepository;
import lamart.lkvms.core.repositories.RegistrationInviteRepository;
import lamart.lkvms.core.utilities.common.ClientInfoParser;
import lamart.lkvms.core.utilities.exceptions.UserIsNotInOrganizationException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final OrganizationService organizationService;
    private final AuthService authService;
    private final EmailConfirmationService emailConfirmationService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CodeService codeService;
    private final SessionService sessionService;
    private final DocumentRepository documentRepository;
    private final ConsentRepository consentRepository;
    private final RegistrationInviteRepository inviteRepository;

    @Value("${TELEGRAM_BOT_LINK}")
    private String telegramBotLink;

    UserController(OrganizationService organizationService, AuthService authService, EmailConfirmationService emailConfirmationService, UserService userService, PasswordEncoder passwordEncoder, EmailService emailService, CodeService codeService, SessionService sessionService, DocumentRepository documentRepository, ConsentRepository consentRepository, RegistrationInviteRepository inviteRepository) {
        this.organizationService = organizationService;
        this.authService = authService;
        this.emailConfirmationService = emailConfirmationService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.codeService = codeService;
        this.sessionService = sessionService;
        this.documentRepository = documentRepository;
        this.consentRepository = consentRepository;
        this.inviteRepository = inviteRepository;
    }

    @GetMapping("/current/")
    public ResponseEntity<CurrentUserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());
        CurrentUserDto dto = new CurrentUserDto(
            user.getName(),
            user.getPhoneNumber(),
            user.getEmail(),
            user.getOrganizations().stream().map(OrganizationMapper::toDto).toList(),
            OrganizationMapper.toDto(user.getSelectedOrganization()));
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/login/token-refresh/")
    public ResponseEntity<AccessTokenResponse> refreshToken(
            @RequestBody TokenRefreshRequest request) {
                return ResponseEntity.ok(authService.refreshToken(request));
        }

    @PostMapping("/login/")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String ip = ClientInfoParser.getIpAddress(httpRequest);
        return ResponseEntity.ok(authService.authenticate(
            request,
            ClientInfoParser.getBrowser(httpRequest),
            ClientInfoParser.getDevice(httpRequest),
            ip
        ));
    }

    @PostMapping("/register/")
    public ResponseEntity<?> registerUser(@org.springframework.web.bind.annotation.RequestBody UserRegisterDto request) {

        EmailConfirmation emailConfirmation = emailConfirmationService.getEmailConfirmation(
            UUID.fromString(request.emailConfirmationId), 
            true
        );
        
        if (!emailConfirmation.isConfirmed()) {
            return ResponseEntity.badRequest()
                .body(new EntityNotFoundException("Wrong email confirmation code"));
        }

        String email = emailConfirmation.getEmail().toLowerCase();
        userService.updateOrCreateUserWithEmailUsername(
            email,
            request.password,
            request.name,
            true
        );

        return ResponseEntity.status(HttpStatus.CREATED)
               .body(String.format("User %s created", request.name));
    }

    @PatchMapping("/change-password/")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody PasswordChangeDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (request.newPassword == null || !request.newPassword.equals(request.confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "New passwords don't match"));
        }
        User user = userService.getUserByEmail(userDetails.getUsername());

        if (!passwordEncoder.matches(request.currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Current password is incorrect"));
        }

        user.setPassword(passwordEncoder.encode(request.newPassword));
        userService.updateUser(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    

    @PostMapping("/password/reset/required")
    public ResponseEntity<String> askToResetPassword(
            @RequestBody PasswordResetRequestDto request,
            HttpServletRequest httpRequest) {

        String email = request.email.toLowerCase();

        if (userService.getUserByEmail(email) != null) {
            emailService.sendPasswordResetEmail(email, httpRequest.getRemoteAddr());
            return ResponseEntity.ok("Sent email with code to reset password.");
        }
        
        return ResponseEntity.badRequest().body("Failed to find user with email.");
    }


    @PostMapping("/password/reset")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordDto request) {

        String email = request.email.toLowerCase();

        User user = userService.getUserByEmail(email);
                
        if (user == null) {
            return ResponseEntity.badRequest().body("Sent incorrect email.");
        }
        
        String storedCode = codeService.getOrCreatePasswordResetCode(email);
        if (!storedCode.equals(request.code)) {
            return ResponseEntity.badRequest().body("Sent incorrect code.");
        }

        codeService.deletePasswordResetCode(email);
        user.setPassword(passwordEncoder.encode(request.newPassword));
        userService.updateUser(user);
        
        return ResponseEntity.ok("Password reset successfully.");
    }

    @PatchMapping("/update-profile/")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserProfileUpdateDto dto) {
        
        User user = userService.getUserByEmail(userDetails.getUsername());
        try{
            if (dto.name != null) user.setName(dto.name);
            if (dto.phoneNumber != null) user.setPhoneNumber(dto.phoneNumber);
            if (dto.email != null) user.setEmail(dto.email.toLowerCase());
            if (dto.selectedOrganization != null) {
                user.setSelectedOrganization(
                    organizationService.getOrganizationById(dto.selectedOrganization)
                );
            }
            
            userService.updateUser(user);
            return ResponseEntity.ok().build();
        }
        catch(UserIsNotInOrganizationException e){
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/sessions/")
    public List<SessionDto> getSessions(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getUserByEmail(userDetails.getUsername());
        return SessionMapper.convertToDtoList(
            sessionService.getSessions(user)
        );
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userService.getUserByEmail(userDetails.getUsername());
        Session session = sessionService.findSessionById(id);

        if (!session.getUser().equals(user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        
        sessionService.deleteSession(session);
        return ResponseEntity.noContent().build();
    }  
    
    @PostMapping("/send-confirmation-code")
    public ResponseEntity<EmailConfirmationResponseDto> createConfirmation(
            @RequestBody PasswordResetRequestDto request) {

        String email = request.email.toLowerCase();

        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest()
                .body(new EmailConfirmationResponseDto("This is not an authorized email"));
        }

        EmailConfirmation confirmation = emailConfirmationService.createEmailConfirmation(email);
        return ResponseEntity.ok(new EmailConfirmationResponseDto(confirmation));
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<EmailConfirmationResponseDto> confirmEmail(
            @RequestBody ConfirmEmailRequestDto request) {

        EmailConfirmation confirmation = emailConfirmationService.getEmailConfirmationWithChecks(
            request.emailConfirmationId, 
            Integer.parseInt(request.code)
        ).orElseThrow();

        if (confirmation == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new EmailConfirmationResponseDto("No email confirmation with this ID"));
        }

        if (confirmation.isConfirmed()) {
            return ResponseEntity.ok(new EmailConfirmationResponseDto(confirmation));
        }

        else {
            return ResponseEntity.badRequest()
                .body(new EmailConfirmationResponseDto("Wrong email confirmation code"));
        }
    }

    @GetMapping("/document")
    public ResponseEntity<Resource> getDocument(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String title) {

        if (id == null && title == null) {
            return ResponseEntity.badRequest().build();
        }

        Document document = id != null 
            ? documentRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))
            : documentRepository.findByTitle(title)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ByteArrayResource resource = new ByteArrayResource(document.getContent());
        
        return ResponseEntity.ok()
            .contentType(getMediaType(document.getTitle()))
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                   "inline; filename=\"" + document.getTitle() + "\"")
            .body(resource);
    }

    private MediaType getMediaType(String filename) {
        if (filename.toLowerCase().endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    @PostMapping(value = "/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("content") MultipartFile file) {

        User user = userService.getUserByEmail(userDetails.getUsername());
        if (!user.getRoles().contains(userService.getSuperuserRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to perform this action"));
        }

        if (documentRepository.existsByTitle(file.getOriginalFilename())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Document with this name already exists"));
        }

        try {
            Document document = new Document();
            document.setTitle(file.getOriginalFilename());
            document.setContent(file.getBytes());
            documentRepository.save(document);

            return ResponseEntity.ok(Map.of("status", "Success"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error uploading document"));
        }
    }

    @PutMapping(value = "/update-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> updateDocument(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("content") MultipartFile file) {

        User user = userService.getUserByEmail(userDetails.getUsername());
        if (!user.getRoles().contains(userService.getSuperuserRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "You do not have permission to perform this action"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is required"));
        }

        Document document = documentRepository.findByTitle(file.getOriginalFilename())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found"));

        try {
            document.setTitle(file.getOriginalFilename());
            document.setContent(file.getBytes());
            document.setUpdatedAt(LocalDateTime.now());
            documentRepository.save(document);

            return ResponseEntity.ok(Map.of("status", "Success"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error updating document"));
        }
    }

    @GetMapping("/check-consent")
    public ResponseEntity<?> checkConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String doc_name) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        if (doc_name == null || doc_name.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Document name not provided"));
        }

        User user = userService.getUserByEmail(userDetails.getUsername());
        UUID userId = user.getId();

        Optional<Document> documentOpt = documentRepository.findByTitle(doc_name);
        if (documentOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("is_consenting_to_cookies", false));
        }

        Document document = documentOpt.get();
        Optional<Consent> consentOpt = consentRepository.findByUserIdAndDocId(userId, document.getId());

        if (consentOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("is_consenting_to_cookies", false));
        }

        Consent consent = consentOpt.get();
        LocalDateTime lastDocUpdate = document.getUpdatedAt().isAfter(document.getUploadedAt()) 
            ? document.getUpdatedAt() 
            : document.getUploadedAt();

        boolean isValid = consent.getUpdatedAt().isAfter(lastDocUpdate);
        return ResponseEntity.ok(Map.of("is_consenting_to_cookies", isValid));
    }

    @PutMapping("/update-consent")
    public ResponseEntity<ConsentDto> createOrUpdateConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String doc_name,
            @Valid @RequestBody ConsentDto consentDto) {

        User currentUser = userService.getUserByEmail(userDetails.getUsername());

        Document requestedDocument = documentRepository.findByTitle(doc_name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid document name"));

        Consent consent = consentRepository.findByUserIdAndDocId(currentUser.getId(), requestedDocument.getId())
                .orElseGet(() -> {
                    Consent newConsent = new Consent();
                    newConsent.setUser(currentUser);
                    newConsent.setDoc(requestedDocument);
                    return newConsent;
                });

        consent.setUpdatedAt(LocalDateTime.now());
        Consent savedConsent = consentRepository.save(consent);

        return ResponseEntity.ok(ConsentMapper.toDto(savedConsent));
    }

    @GetMapping("/telegram/bot")
    public ResponseEntity<String> getTelegramBotInfo(
            @Valid @RequestBody TelegramChatLinkRequest request) {
        
        String username = codeService.getUsernameFromTelegramCode(request.userCode);
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user is linked to this code.");
        }

        userService.linkUserWithTelegramBotChat(username, request.chatId);
        return ResponseEntity.ok("Chat linked with user.");
    }

    @DeleteMapping("/telegram/bot")
    public ResponseEntity<String> deleteTelegramBotInfo(
            @Valid @RequestBody TelegramChatRequest request) {
        
        boolean unlinked = userService.unlinkUserFromTelegramBotChat(request.chatId);
        if (!unlinked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user linked to this chat.");
        }

        return ResponseEntity.ok("Chat unlinked from user.");
    }

    @GetMapping("/telegram/link")
    public ResponseEntity<TelegramLinkDto> getLinkToTelegram(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String username = userDetails.getUsername();
        
        if (userService.isUserLinkedToTelegramBot(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "User already linked to Telegram bot chat.");
        }

        String code = codeService.getOrCreateUsersTelegramLinkCode(username);
        TelegramLinkDto response = new TelegramLinkDto();
        response.userCode = code;
        response.link = telegramBotLink + "?start=" + code;
        
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/telegram/unlink")
    public ResponseEntity<String> unlinkTelegram(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.getUserByEmail(userDetails.getUsername());
        
        if (!userService.isUserLinkedToTelegramBot(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "No user linked to this chat.");
        }

        userService.unlinkUserFromTelegramBotChat(user.getTelegramChatId());
        userService.stopTelegramChat(user.getTelegramChatId());
        
        return ResponseEntity.ok("Chat unlinked from user.");
    }

    @GetMapping("/invites/unsubscribe/{invite_id}")
    public ResponseEntity<String> unsubscribeFromInvite(@PathVariable String invite_id) {
        
        if (invite_id != null && !invite_id.isEmpty()) {
            try {
                UUID uuid = UUID.fromString(invite_id);
                inviteRepository.findById(uuid).ifPresent(invite -> inviteRepository.delete(invite));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Некорректный id");
            }
        }

        return ResponseEntity.ok("Вы отписаны от рассылки");
    }
}
