package com.smarttrip.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SmartTrip API",
                version = "1.0",
                description = "API REST du POC SmartTrip, assistant de voyage intelligent."
        ),
        tags = {
                @Tag(
                        name = "Trips",
                        description = "Gestion des voyages et des itinéraires"
                ),
                @Tag(
                        name = "Chat",
                        description = "Interaction avec l'assistant IA SmartTrip"
                )
        }
)
public class OpenApiConfig {
}