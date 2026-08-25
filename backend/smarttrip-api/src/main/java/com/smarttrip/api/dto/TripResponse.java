package com.smarttrip.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Informations d'un voyage")
public record TripResponse(

        @Schema(
                description = "Identifiant unique du voyage",
                example = "1"
        )
        Long id,

        @Schema(
                description = "Destination du voyage",
                example = "Rome"
        )
        String destination,

        @Schema(
                description = "Date de début du voyage",
                example = "2026-09-15"
        )
        LocalDate startDate,

        @Schema(
                description = "Date de fin du voyage",
                example = "2026-09-20"
        )
        LocalDate endDate,

        @Schema(
                description = "Nombre de voyageurs",
                example = "2"
        )
        int travelers,

        @Schema(
                description = "Préférences des voyageurs",
                example = "Histoire, gastronomie et visites culturelles"
        )
        String preferences
) {
}