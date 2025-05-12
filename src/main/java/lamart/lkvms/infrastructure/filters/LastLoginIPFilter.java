package lamart.lkvms.infrastructure.filters;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lamart.lkvms.application.services.user.UserService;
import lamart.lkvms.core.entities.user.User;

@Component
public class LastLoginIPFilter implements Filter {

    private final UserService userService;

    LastLoginIPFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String currentIp = getClientIp(httpRequest);

        if (currentIp != null) {
            request.setAttribute("ip", currentIp);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                    String username = resolveUsername(principal);
                    if (username != null)
                        userService.updateLastLoginIp(username, currentIp);
            }
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String resolveUsername(Object principal) {
    if (principal instanceof UserDetails) {
        return ((UserDetails) principal).getUsername();
    } else if (principal instanceof User) {
        return ((User) principal).getUsername();
    }
    return null;
}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
