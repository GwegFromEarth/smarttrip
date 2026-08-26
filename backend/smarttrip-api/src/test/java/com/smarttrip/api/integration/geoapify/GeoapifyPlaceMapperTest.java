package com.smarttrip.api.integration.geoapify;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeoapifyPlaceMapperTest {

    private final GeoapifyPlaceMapper mapper = new GeoapifyPlaceMapper();

    @Test
    void shouldMapFeatureToPlaceDto() {

        var properties = new GeoapifyProperties(
                "Musée du Louvre",
                "Un célèbre musée parisien",
                48.8606,
                2.3376,
                List.of(
                        "entertainment.museum",
                        "tourism.sights"
                )
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var result = mapper.toPlaceDto(feature);

        assertEquals("Musée du Louvre", result.name());
        assertEquals("Un célèbre musée parisien", result.description());
        assertEquals(48.8606, result.latitude());
        assertEquals(2.3376, result.longitude());
        assertEquals("entertainment.museum", result.category());
    }

    @Test
    void shouldReturnFirstCategoryWhenNoEntertainmentCategoryExists() {

        var properties = new GeoapifyProperties(
                "Supermarket",
                "Supermarket in Paris",
                48.8500,
                2.3500,
                List.of(
                        "commercial.supermarket",
                        "commercial"
                )
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var result = mapper.toPlaceDto(feature);

        assertEquals(
                "commercial.supermarket",
                result.category()
        );
    }

    @Test
    void shouldThrowExceptionWhenPropertiesAreMissing() {

        var feature = new GeoapifyFeature(
                "Feature",
                null
        );

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toPlaceDto(feature)
        );

        assertEquals(
                "Geoapify feature has no properties",
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenCoordinatesAreMissing() {

        var properties = new GeoapifyProperties(
                "Lieu sans coordonnées",
                "Description",
                null,
                null,
                List.of("entertainment.museum")
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toPlaceDto(feature)
        );

        assertEquals(
                "Geoapify feature has no coordinates: Lieu sans coordonnées",
                exception.getMessage()
        );
    }
}