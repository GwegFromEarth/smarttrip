package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.foursquare.FoursquarePlaceService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaceServiceTest {

    private final FoursquarePlaceService foursquarePlaceService =
            mock(FoursquarePlaceService.class);

    private final PlaceRankingService placeRankingService =
            new PlaceRankingService();

    private final PlaceService placeService =
            new PlaceService(
                    foursquarePlaceService,
                    placeRankingService
            );

    @Test
    void shouldSearchPlaces() {

        var place = new PlaceDto(
                "louvre-test-id",
                "Louvre Museum",
                null,
                48.8606,
                2.3376,
                PlaceCategory.MUSEUM,
                "Rue de Rivoli, 75001 Paris, France",
                100.0
        );

        when(foursquarePlaceService.searchPlaces(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                50
        )).thenReturn(List.of(place));

        var result = placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertEquals(1, result.size());
        assertEquals("Louvre Museum", result.get(0).name());
        assertEquals(48.8606, result.get(0).latitude());
        assertEquals(2.3376, result.get(0).longitude());
        assertEquals(PlaceCategory.MUSEUM, result.get(0).category());
    }

    @Test
    void shouldReturnEmptyListWhenFoursquareReturnsNoPlaces() {

        when(foursquarePlaceService.searchPlaces(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                50
        )).thenReturn(List.of());

        var result = placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldSearchPlacesByDestination() {

        var place = new PlaceDto(
                "colosseum-test-id",
                "Colosseum",
                null,
                41.8902,
                12.4922,
                PlaceCategory.TOURIST_ATTRACTION,
                "Piazza del Colosseo, 1, Rome, Italy",
                500.0
        );

        when(foursquarePlaceService.searchByDestination(
                "Rome",
                PlaceCategory.TOURIST_ATTRACTION,
                50
        )).thenReturn(List.of(place));

        var result = placeService.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        );

        assertEquals(1, result.size());
        assertEquals("Colosseum", result.get(0).name());
        assertEquals(41.8902, result.get(0).latitude());
        assertEquals(12.4922, result.get(0).longitude());
        assertEquals(
                PlaceCategory.TOURIST_ATTRACTION,
                result.get(0).category()
        );
    }

    @Test
    void shouldReturnEmptyListWhenFoursquareReturnsNoPlacesByDestination() {

        when(foursquarePlaceService.searchByDestination(
                "Rome",
                PlaceCategory.MUSEUM,
                50
        )).thenReturn(List.of());

        var result = placeService.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldRejectInvalidLatitude() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        91.0,
                        2.3376,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        assertEquals(
                "Latitude must be between -90 and 90",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectInvalidLongitude() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        181.0,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        assertEquals(
                "Longitude must be between -180 and 180",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectInvalidRadius() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        0,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        assertEquals(
                "Radius must be at least 1 meter",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectInvalidLimit() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        1000,
                        PlaceCategory.MUSEUM,
                        101
                )
        );

        assertEquals(
                "Limit must be between 1 and 100",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullCategory() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        1000,
                        null,
                        10
                )
        );

        assertEquals(
                "Category must not be null",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankDestination() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        " ",
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        assertEquals(
                "Destination must not be blank",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullCategoryByDestination() {

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        "Rome",
                        1000,
                        null,
                        10
                )
        );

        assertEquals(
                "Category must not be null",
                exception.getMessage()
        );
    }
}