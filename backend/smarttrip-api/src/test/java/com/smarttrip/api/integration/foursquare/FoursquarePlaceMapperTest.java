package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FoursquarePlaceMapperTest {

    private final FoursquarePlaceMapper mapper =
            new FoursquarePlaceMapper();

    @Test
    void shouldMapPlaceToPlaceDto() {

        FoursquarePlace place = new FoursquarePlace(
                "fsq-123",
                "Louvre Museum",
                48.8606,
                2.3376,
                new FoursquareLocation(
                        "Rue de Rivoli",
                        "Paris",
                        "Île-de-France",
                        "75001",
                        "France"
                ),
                List.of(),
                150,
                9.2,
                0.85
        );

        PlaceDto result = mapper.toPlaceDto(
                place,
                PlaceCategory.MUSEUM
        );

        assertEquals("fsq-123", result.placeId());
        assertEquals("Louvre Museum", result.name());
        assertEquals(48.8606, result.latitude());
        assertEquals(2.3376, result.longitude());
        assertEquals(PlaceCategory.MUSEUM, result.category());

        assertEquals(
                "Rue de Rivoli, 75001, Paris, Île-de-France, France",
                result.address()
        );

        assertEquals(150.0, result.distance());
        assertEquals(9.2, result.rating());
        assertEquals(0.85, result.popularity());
        assertNull(result.description());
    }

    @Test
    void shouldReturnNullAddressWhenLocationIsNull() {

        FoursquarePlace place = new FoursquarePlace(
                "fsq-456",
                "Colosseum",
                41.8902,
                12.4922,
                null,
                List.of(),
                500,
                9.5,
                0.95
        );

        PlaceDto result = mapper.toPlaceDto(
                place,
                PlaceCategory.TOURIST_ATTRACTION
        );

        assertEquals("fsq-456", result.placeId());
        assertEquals("Colosseum", result.name());
        assertNull(result.address());
    }

    @Test
    void shouldReturnNullDistanceWhenDistanceIsNull() {

        FoursquarePlace place = new FoursquarePlace(
                "fsq-789",
                "Test Museum",
                48.8606,
                2.3376,
                null,
                List.of(),
                null,
                8.5,
                0.70
        );

        PlaceDto result = mapper.toPlaceDto(
                place,
                PlaceCategory.MUSEUM
        );

        assertNull(result.distance());
        assertEquals(8.5, result.rating());
        assertEquals(0.70, result.popularity());
    }

    @Test
    void shouldIgnoreBlankAddressParts() {

        FoursquarePlace place = new FoursquarePlace(
                "fsq-999",
                "Test Place",
                48.8566,
                2.3522,
                new FoursquareLocation(
                        "10 Rue de Paris",
                        "Paris",
                        "",
                        null,
                        "France"
                ),
                List.of(),
                200,
                null,
                null
        );

        PlaceDto result = mapper.toPlaceDto(
                place,
                PlaceCategory.TOURIST_ATTRACTION
        );

        assertEquals(
                "10 Rue de Paris, Paris, France",
                result.address()
        );
    }

    @Test
    void shouldReturnNullAddressWhenAllAddressPartsAreEmpty() {

        FoursquarePlace place = new FoursquarePlace(
                "fsq-empty",
                "Unknown Place",
                48.8566,
                2.3522,
                new FoursquareLocation(
                        null,
                        null,
                        null,
                        null,
                        null
                ),
                List.of(),
                100,
                null,
                null
        );

        PlaceDto result = mapper.toPlaceDto(
                place,
                PlaceCategory.MUSEUM
        );

        assertNull(result.address());
    }

}
