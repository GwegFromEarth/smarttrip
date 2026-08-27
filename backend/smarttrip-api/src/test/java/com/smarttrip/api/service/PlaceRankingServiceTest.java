package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceRankingServiceTest {

    private final PlaceRankingService placeRankingService =
            new PlaceRankingService();

    @Test
    void shouldRankPlacesByDistance() {

        var far = createPlace("Far", 500.0);
        var close = createPlace("Close", 100.0);
        var medium = createPlace("Medium", 250.0);

        var result = placeRankingService.rank(
                List.of(far, close, medium),
                10
        );

        assertEquals(3, result.size());
        assertEquals("Close", result.get(0).name());
        assertEquals("Medium", result.get(1).name());
        assertEquals("Far", result.get(2).name());
    }

    @Test
    void shouldRespectLimit() {

        var first = createPlace("First", 100.0);
        var second = createPlace("Second", 200.0);
        var third = createPlace("Third", 300.0);

        var result = placeRankingService.rank(
                List.of(third, first, second),
                2
        );

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).name());
        assertEquals("Second", result.get(1).name());
    }

    @Test
    void shouldReturnEmptyListWhenPlacesAreEmpty() {

        var result = placeRankingService.rank(
                List.of(),
                10
        );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldPutPlacesWithoutDistanceLast() {

        var withoutDistance = createPlace("Unknown distance", null);
        var close = createPlace("Close", 100.0);
        var far = createPlace("Far", 500.0);

        var result = placeRankingService.rank(
                List.of(withoutDistance, far, close),
                10
        );

        assertEquals(3, result.size());
        assertEquals("Close", result.get(0).name());
        assertEquals("Far", result.get(1).name());
        assertEquals("Unknown distance", result.get(2).name());
    }

    private PlaceDto createPlace(
            String name,
            Double distance
    ) {
        return new PlaceDto(
                name + "-id",
                name,
                null,
                41.8902,
                12.4922,
                PlaceCategory.TOURIST_ATTRACTION,
                null,
                distance
        );
    }
}