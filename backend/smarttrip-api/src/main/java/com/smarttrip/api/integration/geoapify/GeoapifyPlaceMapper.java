package com.smarttrip.api.integration.geoapify;

import com.smarttrip.api.dto.PlaceDto;
import org.springframework.stereotype.Component;

@Component
public class GeoapifyPlaceMapper {

    public PlaceDto toPlaceDto(GeoapifyFeature feature) {

        var properties = feature.properties();

        if (properties == null) {
            throw new IllegalArgumentException(
                    "Geoapify feature has no properties"
            );
        }

        if (properties.lat() == null || properties.lon() == null) {
            throw new IllegalArgumentException(
                    "Geoapify feature has no coordinates: "
                            + properties.name()
            );
        }

        String category = properties.categories() != null
                ? properties.categories().stream()
                .findFirst()
                .orElse(null)
                : null;

        return new PlaceDto(
                properties.name(),
                properties.description(),
                properties.lat(),
                properties.lon(),
                category
        );
    }
}