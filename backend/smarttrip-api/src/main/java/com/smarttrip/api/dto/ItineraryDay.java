package com.smarttrip.api.dto;

import java.time.LocalDate;
import java.util.List;

public record ItineraryDay(
        int dayNumber,
        LocalDate date,
        List<Activity> activities
) {
}
