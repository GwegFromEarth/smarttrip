package com.smarttrip.api.integration.overpass;

import java.util.Map;

public record OverpassElement(
        String type,
        long id,
        Double lat,
        Double lon,
        Center center,
        Map<String, String> tags
) {

    public record Center(
            double lat,
            double lon
    ) {
    }
}