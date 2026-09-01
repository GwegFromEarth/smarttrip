package com.smarttrip.api.integration.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquareGeocodes(
        FoursquareCoordinates main
) {
}