package com.smarttrip.api.dto;

import java.util.List;

public record Itinerary(
        Long tripId,
        String destination,
        List<ItineraryDay> days
) {
}
