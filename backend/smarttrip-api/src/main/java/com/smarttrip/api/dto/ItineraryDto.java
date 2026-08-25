package com.smarttrip.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Itinéraire généré pour un voyage")
public record ItineraryDto(

        @Schema(
                description = "Identifiant du voyage associé à l'itinéraire",
                example = "1"
        )
        Long tripId,

        @Schema(
                description = "Destination du voyage",
                example = "Rome"
        )
        String destination,

        @Schema(
                description = "Liste des journées composant l'itinéraire"
        )
        List<ItineraryDayDto> days
) {
}