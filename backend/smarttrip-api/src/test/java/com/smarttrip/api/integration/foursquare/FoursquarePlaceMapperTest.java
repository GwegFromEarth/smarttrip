package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FoursquarePlaceMapperTest {

    private final FoursquarePlaceMapper mapper = new FoursquarePlaceMapper();

    @Test
    void shouldMapFoursquarePlaceToPlaceDto() {

        FoursquarePlace place = new FoursquarePlace(
                "abc123",
                "Colosseum",
                41.8902,
                12.4922,
                new FoursquareLocation(
                        "Piazza del Colosseo",
                        "Rome",
                        "Lazio",
                        "00184",
                        "Italy"
                ),
                List.of(
                        new FoursquareCategory(
                                "category123",
                                "Historic Site"
                        )
                ),
                250
        );

        PlaceDto result = mapper.toPlaceDto(place, PlaceCategory.TOURIST_ATTRACTION);

        assertNotNull(result);

        assertEquals("abc123", result.placeId());
        assertEquals("Colosseum", result.name());

        assertEquals(41.8902, result.latitude());
        assertEquals(12.4922, result.longitude());

        assertEquals(PlaceCategory.TOURIST_ATTRACTION, result.category());

        assertEquals(
                "Piazza del Colosseo, 00184, Rome, Lazio, Italy",
                result.address()
        );

        assertEquals(250.0, result.distance());
    }
}