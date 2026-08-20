package com.smarttrip.api.integration.overpass;

import java.util.List;

public record OverpassResponse(
        List<OverpassElement> elements
) {
}