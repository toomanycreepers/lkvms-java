package lamart.lkvms.application.services.user;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lamart.lkvms.core.utilities.common.IpChecker;
import lamart.lkvms.core.utilities.exceptions.ApiRateLimitException;

@Service
public class IpLocationService {
    @Value("${ip.api.url:http://ip-api.com/json}")
    private String baseUrl;
    
    @Value("${ip.api.language:en}") 
    private String language;
    
    private Instant rateLimitResetTime;
    private final RestTemplate restTemplate;

    IpLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public Optional<String> getLocation(String ip) {
        return getIpInfo(ip)
            .map(info -> {
                String city = (String) info.get("city");
                String country = (String) info.get("country");
                return (city != null || country != null) 
                    ? String.join(", ", city, country) 
                    : null;
            });
    }
    
    public Optional<Map<String, Object>> getIpInfo(String ip) {
        if (!IpChecker.isCorrectGlobalIp(ip)) {
            return Optional.empty();
        }
        
        if (rateLimitResetTime != null && Instant.now().isBefore(rateLimitResetTime)) {
            throw new ApiRateLimitException();
        }
        
        String url = String.format("%s/%s?lang=%s", baseUrl, ip, language);
        ResponseEntity<Map> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            null, 
            Map.class
        );
        
        handleRateLimitHeaders(response.getHeaders());
        
        if (response.getStatusCode() == HttpStatus.OK && 
            response.getBody() != null && 
            "success".equals(response.getBody().get("status"))) {
            return Optional.of(response.getBody());
        }
        
        return Optional.empty();
    }
    
    private void handleRateLimitHeaders(HttpHeaders headers) {
        String remaining = headers.getFirst("X-Rl");
        String ttl = headers.getFirst("X-Ttl");
        
        if (remaining != null && ttl != null) {
            int remainingRequests = Integer.parseInt(remaining);
            int secondsToReset = Integer.parseInt(ttl);
            
            if (remainingRequests <= 5) {
                rateLimitResetTime = Instant.now().plus(Duration.ofSeconds(secondsToReset));
            }
        }
    }
}
