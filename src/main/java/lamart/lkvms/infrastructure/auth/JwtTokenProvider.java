package lamart.lkvms.infrastructure.auth;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lamart.lkvms.core.entities.user.Role;
import lamart.lkvms.core.entities.user.User;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;
 
    @Value("${jwt.access-token-expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshExpiration;
    

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(jwtSecret)
        );
    }

    public String generateAccessToken(Authentication authentication, UUID sessionId) {
        User userPrincipal = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("token_type", "access")
                .claim("user_id", userPrincipal.getId())
                .claim("session", sessionId)
                .claim("roles", userPrincipal.getAuthorities())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
}

    public String generateRefreshToken(Authentication authentication, UUID sessionId) {
        User userPrincipal = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration * 1000);

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(userPrincipal.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("token_type", "refresh")
                .claim("user_id", userPrincipal.getId())
                .claim("session", sessionId)
                .claim("roles", userPrincipal.getAuthorities())
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        User user = new User();
        user.setId(claims.get("id", UUID.class));
        user.setUsername(claims.getSubject());
        List<Role> rolesList = claims.get("roles", List.class);
        Set<Role> roles = new HashSet<>(rolesList);
        user.setRoles(roles);
        
        return new UsernamePasswordAuthenticationToken(
            user, 
            "", 
            user.getAuthorities()
        );
    }
}
