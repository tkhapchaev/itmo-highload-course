package ru.tkhapchaev.voteservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Vote Service API",
                version = "1.0",
                description = "REST API for voting with HATEOAS and Kafka events"
        )
)
public class OpenApiConfig {
}
