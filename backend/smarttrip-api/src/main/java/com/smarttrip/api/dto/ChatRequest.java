package com.smarttrip.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête envoyée à l'assistant IA SmartTrip")
public record ChatRequest(

        @Schema(
                description = "Identifiant de la conversation existante. Null pour démarrer une nouvelle conversation.",
                example = "1"
        )
        Long conversationId,

        @Schema(
                description = "Message envoyé à l'assistant IA",
                example = "Propose-moi trois lieux historiques à Rome"
        )
        String message
) {
}