package com.smarttrip.api.integration.foursquare;

import java.util.List;

public record FoursquareResponse(
        List<FoursquarePlace> results
) {
}