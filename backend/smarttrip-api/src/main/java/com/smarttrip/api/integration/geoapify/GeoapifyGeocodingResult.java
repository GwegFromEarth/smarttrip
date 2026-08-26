package com.smarttrip.api.integration.geoapify;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoapifyGeocodingResult(
        String name,
        String city,
        String country,
        @JsonProperty("country_code")
        String countryCode,
        Double lat,
        Double lon,
        String formatted
) {
}