package lamart.lkvms.application.services.user;

import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lamart.lkvms.application.dtos.AccessTokenResponse;
import lamart.lkvms.application.dtos.LoginRequest;
import lamart.lkvms.application.dtos.TokenRefreshRequest;
import lamart.lkvms.application.dtos.TokenResponse;
import lamart.lkvms.core.entities.user.Session;
import lamart.lkvms.core.entities.user.User;
import lamart.lkvms.core.repositories.UserRepository;
import lamart.lkvms.infrastructure.auth.JwtTokenProvider;

@Service
public class AuthService {

    private final SessionService sessionService;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    AuthService(UserDetailsService userDetailsService, JwtTokenProvider tokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder, SessionService sessionService) {
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionService = sessionService;
    }

    public TokenResponse authenticate(  LoginRequest request, 
                                        String browser, 
                                        String device,
                                        String ip   ) {
        User user = (User) userDetailsService.loadUserByUsername(request.username);

        Session session = sessionService.updateOrCreateSession(
            user, browser, device, ip
        );
                                            
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user,
            null,
            user.getAuthorities()
        );

        String accessToken = tokenProvider.generateAccessToken(authentication, session.getId());
        String refreshToken = tokenProvider.generateRefreshToken(authentication, session.getId());
        
        sessionService.updateSessionRefreshToken(session.getId(), refreshToken);

        return new TokenResponse(
            refreshToken,
            accessToken
        );
    }

    public AccessTokenResponse refreshToken(TokenRefreshRequest request) {
        if (!tokenProvider.validateToken(request.refresh)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        
        String username = tokenProvider.getUsernameFromToken(request.refresh);
        Claims claims = tokenProvider.getClaimsFromToken(request.refresh);
        UUID sessionId = UUID.fromString(claims.get("session", String.class));
        userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        Authentication authentication = tokenProvider.getAuthentication(request.refresh);
        String newAccessToken = tokenProvider.generateAccessToken(authentication, sessionId);
        
        return new AccessTokenResponse(
            newAccessToken
        );
    }
}
