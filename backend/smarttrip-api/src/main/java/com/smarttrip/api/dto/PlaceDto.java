package com.smarttrip.api.dto;

public record PlaceDto(
        String name,
        String description,
        double latitude,
        double longitude,
        String category
) {
}