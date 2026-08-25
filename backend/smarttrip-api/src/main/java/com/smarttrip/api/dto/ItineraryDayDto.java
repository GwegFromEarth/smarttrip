package com.smarttrip.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Journée d'un itinéraire")
public record ItineraryDayDto(

        @Schema(
                description = "Numéro de la journée dans l'itinéraire",
                example = "1"
        )
        int dayNumber,

        @Schema(
                description = "Date correspondant à cette journée",
                example = "2026-09-15"
        )
        LocalDate date,

        @Schema(
                description = "Liste des activités prévues pour cette journée"
        )
        List<ActivityDto> activities
) {
}