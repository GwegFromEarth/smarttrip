package com.smarttrip.api.dto;

import java.util.List;

public record ItineraryDto(
        Long tripId,
        String destination,
        List<ItineraryDayDto> days
) {
}
