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
            mock(FoursquarePlaceMapper.class);

    private final FoursquarePlaceService service =
            new FoursquarePlaceService(
                    foursquareClient,
                    mapper
            );

    @Test
    void shouldSearchAndMapPlaces() {

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

        FoursquareResponse response =
                new FoursquareResponse(List.of(place));

        PlaceDto placeDto = new PlaceDto(
                "abc123",
                "Colosseum",
                null,
                41.8902,
                12.4922,
                PlaceCategory.MUSEUM,
                "Piazza del Colosseo, 00184, Rome, Lazio, Italy",
                250.0
        );

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                "4bf58dd8d48988d181941735",
                10
        )).thenReturn(response);

        when(mapper.toPlaceDto(place, PlaceCategory.MUSEUM))
                .thenReturn(placeDto);

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
        assertEquals("Colosseum", resultPlace.name());
        assertEquals(41.8902, resultPlace.latitude());
        assertEquals(12.4922, resultPlace.longitude());
        assertEquals(PlaceCategory.MUSEUM, resultPlace.category());
        assertEquals(
                "Piazza del Colosseo, 00184, Rome, Lazio, Italy",
                resultPlace.address()
        );
        assertEquals(250.0, resultPlace.distance());

        verify(foursquareClient).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                "4bf58dd8d48988d181941735",
                10
        );

        verify(mapper).toPlaceDto(place, PlaceCategory.MUSEUM);
    }

    @Test
    void shouldReturnEmptyListWhenResponseIsNull() {

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                "4bf58dd8d48988d181941735",
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
                "4bf58dd8d48988d181941735",
                10
        );

        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnEmptyListWhenResponseContainsNoResults() {

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                "4bf58dd8d48988d181941735",
                10
        )).thenReturn(new FoursquareResponse(null));

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
                "4bf58dd8d48988d181941735",
                10
        );

        verifyNoInteractions(mapper);
    }
}