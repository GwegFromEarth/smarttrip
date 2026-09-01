package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.foursquare.FoursquarePlaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private FoursquarePlaceService foursquarePlaceService;

    @Mock
    private PlaceRankingService placeRankingService;

    @InjectMocks
    private PlaceService placeService;

    @Test
    void search_shouldReturnRankedPlaces() {

        var places = List.of(
                new PlaceDto(
                        "id-1",
                        "Louvre Museum",
                        "Museum in Paris",
                        48.8606,
                        2.3376,
                        PlaceCategory.MUSEUM,
                        "Paris",
                        null,
                        null,
                        null
                )
        );

        var rankedPlaces = List.of(places.get(0));

        when(foursquarePlaceService.searchPlaces(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                50
        )).thenReturn(places);

        when(placeRankingService.rank(places, 10))
                .thenReturn(rankedPlaces);

        var result = placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertEquals(rankedPlaces, result);

        verify(foursquarePlaceService).searchPlaces(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                50
        );

        verify(placeRankingService).rank(places, 10);
    }

    @Test
    void searchByDestination_shouldReturnRankedPlaces() {

        var places = List.of(
                new PlaceDto(
                        "colosseum-id",
                        "Colosseum",
                        "Ancient Roman amphitheatre",
                        41.8902,
                        12.4922,
                        PlaceCategory.TOURIST_ATTRACTION,
                        "Rome",
                        null,
                        null,
                        null
                )
        );

        var rankedPlaces = List.of(places.get(0));

        when(foursquarePlaceService.searchByDestination(
                "Rome",
                PlaceCategory.TOURIST_ATTRACTION,
                50
        )).thenReturn(places);

        when(placeRankingService.rank(places, 10))
                .thenReturn(rankedPlaces);

        var result = placeService.searchByDestination(
                "Rome",
                PlaceCategory.TOURIST_ATTRACTION,
                10
        );

        assertEquals(rankedPlaces, result);

        verify(foursquarePlaceService).searchByDestination(
                "Rome",
                PlaceCategory.TOURIST_ATTRACTION,
                50
        );

        verify(placeRankingService).rank(places, 10);
    }

    @Test
    void search_shouldRejectInvalidLatitude() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        91,
                        2.3376,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void search_shouldRejectInvalidLongitude() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        181,
                        1000,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void search_shouldRejectRadiusBelowMinimum() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        0,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void search_shouldRejectInvalidLimit() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        1000,
                        PlaceCategory.MUSEUM,
                        101
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void search_shouldRejectNullCategory() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        1000,
                        null,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void searchByDestination_shouldRejectBlankDestination() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        "   ",
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void searchByDestination_shouldRejectNullDestination() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        null,
                        PlaceCategory.MUSEUM,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void searchByDestination_shouldRejectInvalidLimit() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        "Rome",
                        PlaceCategory.MUSEUM,
                        101
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }

    @Test
    void searchByDestination_shouldRejectNullCategory() {

        assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        "Rome",
                        null,
                        10
                )
        );

        verifyNoInteractions(
                foursquarePlaceService,
                placeRankingService
        );
    }
}