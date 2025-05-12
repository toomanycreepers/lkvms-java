package lamart.lkvms.infrastructure.configs;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OpenApiCustomizer snakeCaseCustomiser() {
        return openApi -> openApi.getComponents().getSchemas().forEach((name, schema) -> {
            if (schema.getProperties() != null) {
                Map<String, Object> newProperties = new LinkedHashMap<>();
                schema.getProperties().forEach((key, value) -> {
                    String snakeKey = convertCamelToSnake((String)key);
                    newProperties.put(snakeKey, value);
                });
                schema.setProperties(newProperties);
            }
        });
    }

    private String convertCamelToSnake(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}