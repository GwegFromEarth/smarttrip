package com.smarttrip.api.integration.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareLocation(
        String address,
        String locality,
        String region,
        String postcode,
        String country
) {
}