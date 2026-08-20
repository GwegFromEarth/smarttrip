package com.smarttrip.api.integration.overpass;

import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OverpassPlaceMapperTest {

    private final OverpassPlaceMapper mapper = new OverpassPlaceMapper();

    @Test
    void shouldMapNodeWithCoordinates() {

        var element = new OverpassElement(
                "node",
                123L,
                41.9019689,
                12.4907414,
                null,
                Map.of(
                        "name", "Quattro Fontane",
                        "description", "Four fountains in Rome",
                        "tourism", "attraction"
                )
        );

        PlaceDto result = mapper.toPlaceDto(element);

        assertEquals("Quattro Fontane", result.name());
        assertEquals("Four fountains in Rome", result.description());
        assertEquals(41.9019689, result.latitude());
        assertEquals(12.4907414, result.longitude());
        assertEquals("attraction", result.category());
    }

    @Test
    void shouldMapWayUsingCenterCoordinates() {

        var element = new OverpassElement(
                "way",
                201568922L,
                null,
                null,
                new OverpassElement.Center(
                        41.9036818,
                        12.4979766
                ),
                Map.of(
                        "name", "Terme di Diocleziano",
                        "description", "Ancient Roman baths",
                        "tourism", "attraction"
                )
        );

        PlaceDto result = mapper.toPlaceDto(element);

        assertEquals("Terme di Diocleziano", result.name());
        assertEquals("Ancient Roman baths", result.description());
        assertEquals(41.9036818, result.latitude());
        assertEquals(12.4979766, result.longitude());
        assertEquals("attraction", result.category());
    }

    @Test
    void shouldRejectElementWithoutCoordinates() {

        var element = new OverpassElement(
                "relation",
                13593496L,
                null,
                null,
                null,
                Map.of(
                        "name", "Museo diffuso del Rione Testaccio",
                        "tourism", "museum"
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toPlaceDto(element)
        );
    }
}