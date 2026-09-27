package ru.tkhapchaev.electionservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Election Service API",
                version = "1.0",
                description = "REST API for elections and candidates with HATEOAS"
        )
)
public class OpenApiConfig {
}
