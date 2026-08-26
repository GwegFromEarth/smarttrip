package com.smarttrip.api.dto;

public record PlaceDto(
        String placeId,
        String name,
        String description,
        double latitude,
        double longitude,
        String category,
        String address
) {
}