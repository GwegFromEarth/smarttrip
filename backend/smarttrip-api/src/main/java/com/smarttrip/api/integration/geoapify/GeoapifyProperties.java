package com.smarttrip.api.integration.geoapify;

import java.util.List;

public record GeoapifyProperties(
        String name,
        String description,
        Double lat,
        Double lon,
        List<String> categories
) {
}