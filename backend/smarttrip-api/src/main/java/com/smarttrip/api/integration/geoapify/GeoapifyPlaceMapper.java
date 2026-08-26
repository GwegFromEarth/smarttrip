package com.smarttrip.api.integration.geoapify;

import com.smarttrip.api.dto.PlaceDto;
import org.springframework.stereotype.Component;

@Component
public class GeoapifyPlaceMapper {

    public PlaceDto toPlaceDto(GeoapifyFeature feature) {
        return toPlaceDto(feature, null);
    }

    public PlaceDto toPlaceDto(
            GeoapifyFeature feature,
            String requestedCategory
    ) {

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

        String category = resolveCategory(
                properties.categories(),
                requestedCategory
        );

        return new PlaceDto(
                properties.placeId(),
                properties.name(),
                properties.description(),
                properties.lat(),
                properties.lon(),
                category,
                properties.formatted()
        );
    }

    private String resolveCategory(
            java.util.List<String> categories,
            String requestedCategory
    ) {

        if (categories == null || categories.isEmpty()) {
            return null;
        }

        if (requestedCategory != null) {
            var requested = categories.stream()
                    .filter(requestedCategory::equals)
                    .findFirst();

            if (requested.isPresent()) {
                return requested.get();
            }
        }

        return categories.stream()
                .max(java.util.Comparator.comparingInt(
                        value -> value.split("\\.").length
                ))
                .orElse(null);
    }
}