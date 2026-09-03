package com.smarttrip.api.integration.foursquare;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FoursquarePlace(

        @JsonProperty("fsq_place_id")
        String fsq_id,

        String name,

        Double latitude,

        Double longitude,

        FoursquareGeocodes geocodes,

        FoursquareLocation location,

        List<FoursquareCategory> categories,

        Integer distance,

        Double rating,

        Double popularity
) {
}