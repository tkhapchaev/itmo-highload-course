package ru.tkhapchaev.voterservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Voter Service API",
                version = "1.0",
                description = "REST API for users and voter registration with HATEOAS"
        )
)
public class OpenApiConfig {
}
