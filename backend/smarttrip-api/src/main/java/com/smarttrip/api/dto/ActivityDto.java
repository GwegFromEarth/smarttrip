package com.smarttrip.api.dto;

public record ActivityDto(
        String time,
        String title,
        String description,
        String location
) {
}
