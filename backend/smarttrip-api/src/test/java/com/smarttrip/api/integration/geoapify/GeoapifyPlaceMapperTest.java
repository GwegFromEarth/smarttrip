package com.smarttrip.api.integration.geoapify;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoapifyPlaceMapperTest {

    private final GeoapifyPlaceMapper mapper =
            new GeoapifyPlaceMapper();

    @Test
    void shouldMapFeatureToPlaceDto() {

        var properties = new GeoapifyProperties(
                "louvre-test-id",
                "Louvre Museum",
                "Musée du Louvre",
                48.8606,
                2.3376,
                List.of(
                        "entertainment.museum",
                        "tourism"
                ),
                "Rue de Rivoli, 75001 Paris, France"
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var result = mapper.toPlaceDto(feature);

        assertEquals("louvre-test-id", result.placeId());
        assertEquals("Louvre Museum", result.name());
        assertEquals("Musée du Louvre", result.description());
        assertEquals(48.8606, result.latitude());
        assertEquals(2.3376, result.longitude());
        assertEquals("entertainment.museum", result.category());
        assertEquals(
                "Rue de Rivoli, 75001 Paris, France",
                result.address()
        );
    }

    @Test
    void shouldReturnNullCategoryWhenNoEntertainmentCategoryExists() {

        var properties = new GeoapifyProperties(
                "supermarket-test-id",
                "Supermarket",
                "Supermarket in Paris",
                48.8606,
                2.3376,
                List.of("commercial.supermarket"),
                "Paris, France"
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var result = mapper.toPlaceDto(feature);

        assertNull(result.category());
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
                "missing-coordinates-test-id",
                "Lieu sans coordonnées",
                "Lieu sans coordonnées",
                null,
                null,
                List.of("entertainment.museum"),
                "Paris, France"
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