package com.smarttrip.api.integration.geoapify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeoapifyProperties(
        @JsonProperty("place_id")
        String placeId,

        String name,

        String description,

        Double lat,

        Double lon,

        List<String> categories,

        String formatted,

        Double distance
) {
}