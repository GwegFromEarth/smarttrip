package com.smarttrip.api.integration.geoapify;

import java.util.List;

public record GeoapifyGeocodingResponse(
        List<GeoapifyGeocodingResult> results
) {
}