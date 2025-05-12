package lamart.lkvms.infrastructure.configs;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        final Integer TELEGRAM_CACHE_TIMEOUT = 7200; // 2 hours in seconds
        final Integer PASSWORD_RESET_TIMEOUT = 420; // 7 minutes in seconds

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        cacheConfigurations.put("telegram", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(TELEGRAM_CACHE_TIMEOUT)));
        
        cacheConfigurations.put("pwd", 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(PASSWORD_RESET_TIMEOUT)));
        
        RedisCacheConfiguration defaultConfig = 
            RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30));
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(cacheConfigurations)
            .build();
    }
}
