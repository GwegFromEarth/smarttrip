package com.smarttrip.api.dto;

public record Activity(
        String time,
        String title,
        String description,
        String location
) {
}
