package com.smarttrip.api.integration.geoapify;

import java.util.List;

public record GeoapifyResponse(
        String type,
        List<GeoapifyFeature> features
) {
}