package com.konstde00.tenant_management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@io.swagger.v3.oas.annotations.security.SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "Demo data center api documentation",
                description = "Demo data center api documentation", version = "v3"),
        security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "basicAuth")
)
public class SwaggerConfig {

    @Bean
    public OpenAPI springOpenAPI() {

        return new OpenAPI()
                .info(new Info().title("Demo data center API")
                        .description("Demo data center api documentation")
                        .version("v3"));
    }
}

