package com.smarttrip.api.dto;

import java.time.LocalDate;

public record TripResponse(
        Long id,
        String destination,
        LocalDate startDate,
        LocalDate endDate,
        int travelers,
        String preferences
) {
}
