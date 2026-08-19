package com.smarttrip.api.dto;

import java.time.LocalDate;
import java.util.List;

public record ItineraryDayDto(
        int dayNumber,
        LocalDate date,
        List<ActivityDto> activities
) {
}
