package com.smarttrip.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Activité prévue dans une journée de l'itinéraire")
public record ActivityDto(

        @Schema(
                description = "Heure prévue pour l'activité",
                example = "09:00"
        )
        String time,

        @Schema(
                description = "Titre de l'activité",
                example = "Visite du Colisée"
        )
        String title,

        @Schema(
                description = "Description de l'activité",
                example = "Découverte du Colisée et de son histoire antique"
        )
        String description,

        @Schema(
                description = "Lieu où se déroule l'activité",
                example = "Colisée, Rome"
        )
        String location
) {
}