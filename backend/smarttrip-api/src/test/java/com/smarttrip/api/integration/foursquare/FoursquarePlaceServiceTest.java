package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoursquarePlaceServiceTest {

    private final FoursquareClient foursquareClient =
            mock(FoursquareClient.class);

    private final FoursquarePlaceMapper mapper =
            new FoursquarePlaceMapper();

    private final FoursquarePlaceService service =
            new FoursquarePlaceService(
                    foursquareClient,
                    mapper
            );

    @Test
    void shouldSearchAndMapPlaces() {

        FoursquarePlace place = new FoursquarePlace(
                "abc123",
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
                List.of(
                        new FoursquareCategory(
                                "category123",
                                "Museum"
                        )
                ),
                250,
                4.5,
                95.0
        );

        FoursquareResponse response = new FoursquareResponse(
                List.of(place)
        );

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                10
        )).thenReturn(response);

        List<PlaceDto> result = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(result);
        assertEquals(1, result.size());

        PlaceDto resultPlace = result.get(0);

        assertEquals("abc123", resultPlace.placeId());
        assertEquals("Louvre Museum", resultPlace.name());
        assertEquals(48.8606, resultPlace.latitude());
        assertEquals(2.3376, resultPlace.longitude());
        assertEquals(PlaceCategory.MUSEUM, resultPlace.category());

        assertEquals(
                "Rue de Rivoli, 75001, Paris, Île-de-France, France",
                resultPlace.address()
        );

        assertEquals(250.0, resultPlace.distance());
        assertEquals(4.5, resultPlace.rating());
        assertEquals(95.0, resultPlace.popularity());

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldReturnEmptyListWhenResponseIsNull() {

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                10
        )).thenReturn(null);

        List<PlaceDto> result = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldReturnEmptyListWhenResponseContainsNoResults() {

        FoursquareResponse response = new FoursquareResponse(
                List.of()
        );

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                10
        )).thenReturn(response);

        List<PlaceDto> result = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldUseTouristAttractionCategoryId() {

        FoursquareResponse response = new FoursquareResponse(
                List.of()
        );

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                null,
                "4bf58dd8d48988d181941735",
                10
        )).thenReturn(response);

        List<PlaceDto> result = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                null,
                "4bf58dd8d48988d181941735",
                10
        );
    }

    @Test
    void shouldLimitSearchToMaximumAllowedLimit() {

        FoursquareResponse response = new FoursquareResponse(
                List.of()
        );

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                50
        )).thenReturn(response);

        List<PlaceDto> result = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                100
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                50
        );
    }

    @Test
    void shouldSearchPlacesByDestination() {

        FoursquarePlace place = new FoursquarePlace(
                "colosseum123",
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
                100,
                4.8,
                98.0
        );

        FoursquareResponse response = new FoursquareResponse(
                List.of(place)
        );

        when(foursquareClient.searchByDestination(
                "Rome",
                null,
                "4bf58dd8d48988d181941735",
                10
        )).thenReturn(response);

        List<PlaceDto> result = service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        );

        assertNotNull(result);
        assertEquals(1, result.size());

        PlaceDto resultPlace = result.get(0);

        assertEquals("colosseum123", resultPlace.placeId());
        assertEquals("Colosseum", resultPlace.name());
        assertEquals(41.8902, resultPlace.latitude());
        assertEquals(12.4922, resultPlace.longitude());

        assertEquals(
                PlaceCategory.TOURIST_ATTRACTION,
                resultPlace.category()
        );

        assertEquals(
                "Piazza del Colosseo, 00184, Rome, Lazio, Italy",
                resultPlace.address()
        );

        assertEquals(100.0, resultPlace.distance());
        assertEquals(4.8, resultPlace.rating());
        assertEquals(98.0, resultPlace.popularity());

        verify(foursquareClient).searchByDestination(
                "Rome",
                null,
                "4bf58dd8d48988d181941735",
                10
        );
    }

    @Test
    void shouldReturnEmptyListWhenDestinationResponseIsNull() {

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(null);

        List<PlaceDto> result = service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foursquareClient).searchByDestination(
                "Rome",
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldReturnEmptyListWhenDestinationResponseContainsNoResults() {

        FoursquareResponse response = new FoursquareResponse(
                List.of()
        );

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(response);

        List<PlaceDto> result = service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(foursquareClient).searchByDestination(
                "Rome",
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldRejectNullCategory() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPlaces(
                        41.8902,
                        12.4922,
                        1000,
                        null,
                        10
                )
        );

        assertEquals(
                "Category must not be null",
                exception.getMessage()
        );

        verifyNoInteractions(foursquareClient);
    }

    @Test
    void shouldRejectNullCategoryByDestination() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.searchByDestination(
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

        verifyNoInteractions(foursquareClient);
    }
}