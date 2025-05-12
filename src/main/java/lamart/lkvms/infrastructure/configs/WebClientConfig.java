package lamart.lkvms.infrastructure.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    
    @Value("${TELEGRAM_BOT_n8n_AUTH}")
    private String n8nAuth;

    @Value("${TELEGRAM_BOT_n8n_ENDPOINT}")
    private String n8nEndpoint;

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .baseUrl(n8nEndpoint)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean("telegramWebClient")
    WebClient telegramWebClient(WebClient.Builder builder) {
        return builder
                .defaultHeader("Authorization", n8nAuth)
                .build();
    }
}
