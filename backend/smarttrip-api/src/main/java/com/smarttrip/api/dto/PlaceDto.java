package com.smarttrip.api.dto;

import java.util.List;

public record PlaceDto(

        String placeId,

        String name,

        String description,

        double latitude,

        double longitude,

        PlaceCategory category,

        String address,

        Double distance,

        Double rating,

        Double popularity,

        String tel,

        String website,

        List<String> categories
) {
}