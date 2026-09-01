package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FoursquarePlaceServiceTest {

    private final FoursquareClient foursquareClient =
            mock(FoursquareClient.class);

    private final FoursquarePlaceMapper mapper =
            mock(FoursquarePlaceMapper.class);

    private final FoursquarePlaceService service =
            new FoursquarePlaceService(
                    foursquareClient,
                    mapper
            );

    @Test
    void shouldSearchPlacesForTouristAttraction() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                null,
                "4bf58dd8d48988d181941735",
                "POPULARITY",
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchPlaces(
                        41.8902,
                        12.4922,
                        1000,
                        PlaceCategory.TOURIST_ATTRACTION,
                        10
                );

        assertEquals(List.of(), result);

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                null,
                "4bf58dd8d48988d181941735",
                "POPULARITY",
                10
        );
    }

    @Test
    void shouldSearchMuseumsWithRatingSort() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                48.8606,
                2.3376,
                1000,
                "museum",
                null,
                "RATING",
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchPlaces(
                        48.8606,
                        2.3376,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                );

        assertEquals(List.of(), result);

        verify(foursquareClient).search(
                48.8606,
                2.3376,
                1000,
                "museum",
                null,
                "RATING",
                10
        );
    }

    @Test
    void shouldSearchRestaurantsWithRatingSort() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                48.8566,
                2.3522,
                500,
                "restaurant",
                null,
                "RATING",
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchPlaces(
                        48.8566,
                        2.3522,
                        500,
                        PlaceCategory.RESTAURANT,
                        10
                );

        assertEquals(List.of(), result);

        verify(foursquareClient).search(
                48.8566,
                2.3522,
                500,
                "restaurant",
                null,
                "RATING",
                10
        );
    }

    @Test
    void shouldSearchCafesWithRatingSort() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                48.8566,
                2.3522,
                500,
                "cafe",
                null,
                "RATING",
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchPlaces(
                        48.8566,
                        2.3522,
                        500,
                        PlaceCategory.CAFE,
                        10
                );

        assertEquals(List.of(), result);

        verify(foursquareClient).search(
                48.8566,
                2.3522,
                500,
                "cafe",
                null,
                "RATING",
                10
        );
    }

    @Test
    void shouldSearchParksWithPopularitySort() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                48.8566,
                2.3522,
                1000,
                "park",
                null,
                "POPULARITY",
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchPlaces(
                        48.8566,
                        2.3522,
                        1000,
                        PlaceCategory.PARK,
                        10
                );

        assertEquals(List.of(), result);

        verify(foursquareClient).search(
                48.8566,
                2.3522,
                1000,
                "park",
                null,
                "POPULARITY",
                10
        );
    }

    @Test
    void shouldLimitSearchToMaximumOfTenResults() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                "RATING",
                10
        )).thenReturn(response);

        service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                50
        );

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                "RATING",
                10
        );
    }

    @Test
    void shouldReturnMappedPlaces() {

        FoursquarePlace foursquarePlace =
                mock(FoursquarePlace.class);

        FoursquareResponse response =
                new FoursquareResponse(
                        List.of(foursquarePlace)
                );

        PlaceDto place = new PlaceDto(
                "test-id",
                "Colosseum",
                null,
                41.8902,
                12.4922,
                PlaceCategory.TOURIST_ATTRACTION,
                "Piazza del Colosseo, Rome",
                100.0,
                9.0,
                0.9
        );

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                null,
                "4bf58dd8d48988d181941735",
                "POPULARITY",
                10
        )).thenReturn(response);

        when(mapper.toPlaceDto(
                foursquarePlace,
                PlaceCategory.TOURIST_ATTRACTION
        )).thenReturn(place);

        List<PlaceDto> result =
                service.searchPlaces(
                        41.8902,
                        12.4922,
                        1000,
                        PlaceCategory.TOURIST_ATTRACTION,
                        10
                );

        assertEquals(1, result.size());
        assertEquals("Colosseum", result.get(0).name());
        assertEquals(41.8902, result.get(0).latitude());
        assertEquals(12.4922, result.get(0).longitude());

        verify(mapper).toPlaceDto(
                foursquarePlace,
                PlaceCategory.TOURIST_ATTRACTION
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
                "RATING",
                10
        )).thenReturn(null);

        List<PlaceDto> result =
                service.searchPlaces(
                        41.8902,
                        12.4922,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldReturnEmptyListWhenResponseResultsAreNull() {

        FoursquareResponse response =
                new FoursquareResponse(null);

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                "RATING",
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchPlaces(
                        41.8902,
                        12.4922,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldRejectNullCategory() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.searchPlaces(
                        41.8902,
                        12.4922,
                        1000,
                        null,
                        10
                )
        );
    }

    @Test
    void shouldSearchByDestination() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(response);

        List<PlaceDto> result =
                service.searchByDestination(
                        "Rome",
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                );

        assertEquals(List.of(), result);

        verify(foursquareClient).searchByDestination(
                "Rome",
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldLimitDestinationSearchToMaximumOfTenResults() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(response);

        service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                50
        );

        verify(foursquareClient).searchByDestination(
                "Rome",
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldReturnMappedPlacesForDestination() {

        FoursquarePlace foursquarePlace =
                mock(FoursquarePlace.class);

        FoursquareResponse response =
                new FoursquareResponse(
                        List.of(foursquarePlace)
                );

        PlaceDto place = new PlaceDto(
                "rome-museum",
                "Roman Museum",
                null,
                41.9,
                12.5,
                PlaceCategory.MUSEUM,
                "Rome, Italy",
                null,
                8.5,
                0.7
        );

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(response);

        when(mapper.toPlaceDto(
                foursquarePlace,
                PlaceCategory.MUSEUM
        )).thenReturn(place);

        List<PlaceDto> result =
                service.searchByDestination(
                        "Rome",
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                );

        assertEquals(1, result.size());
        assertEquals("Roman Museum", result.get(0).name());
        assertEquals("Rome, Italy", result.get(0).address());
    }

}