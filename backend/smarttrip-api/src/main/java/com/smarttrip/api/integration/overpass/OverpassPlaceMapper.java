package com.smarttrip.api.integration.overpass;

import com.smarttrip.api.dto.PlaceDto;

public class OverpassPlaceMapper {

    public PlaceDto toPlaceDto(OverpassElement element) {

        var tags = element.tags();

        String name = tags != null ? tags.get("name") : null;
        String description = tags != null ? tags.get("description") : null;

        double latitude;
        double longitude;

        if (element.lat() != null && element.lon() != null) {
            latitude = element.lat();
            longitude = element.lon();
        } else if (element.center() != null) {
            latitude = element.center().lat();
            longitude = element.center().lon();
        } else {
            throw new IllegalArgumentException(
                    "Overpass element has no coordinates: " + element.id()
            );
        }

        String category = tags != null
                ? tags.get("tourism")
                : null;

        return new PlaceDto(
                name,
                description,
                latitude,
                longitude,
                category
        );
    }
}