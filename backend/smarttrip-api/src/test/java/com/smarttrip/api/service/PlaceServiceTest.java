package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceCategory;
import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.geoapify.GeoapifyClient;
import com.smarttrip.api.integration.geoapify.GeoapifyFeature;
import com.smarttrip.api.integration.geoapify.GeoapifyGeocodingResult;
import com.smarttrip.api.integration.geoapify.GeoapifyPlaceMapper;
import com.smarttrip.api.integration.geoapify.GeoapifyProperties;
import com.smarttrip.api.integration.geoapify.GeoapifyResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaceServiceTest {

    private final GeoapifyClient geoapifyClient =
            mock(GeoapifyClient.class);

    private final GeocodingService geocodingService =
            mock(GeocodingService.class);

    private final GeoapifyPlaceMapper geoapifyPlaceMapper =
            mock(GeoapifyPlaceMapper.class);

    private final PlaceService placeService =
            new PlaceService(
                    geoapifyClient,
                    geocodingService,
                    geoapifyPlaceMapper
            );

    @Test
    void shouldSearchPlaces() {

        var properties = new GeoapifyProperties(
                "louvre-test-id",
                "Louvre Museum",
                "Museum in Paris",
                48.8606,
                2.3376,
                List.of("entertainment.museum"),
                "Rue de Rivoli, 75001 Paris, France"
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var response = new GeoapifyResponse(
                "FeatureCollection",
                List.of(feature)
        );

        var place = new PlaceDto(
                "louvre-test-id",
                "Louvre Museum",
                "Museum in Paris",
                48.8606,
                2.3376,
                "entertainment.museum",
                "Rue de Rivoli, 75001 Paris, France"
        );

        when(geoapifyClient.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        )).thenReturn(response);

        when(geoapifyPlaceMapper.toPlaceDto(feature))
                .thenReturn(place);

        var result = placeService.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        );

        assertEquals(1, result.size());
        assertEquals("Louvre Museum", result.get(0).name());
        assertEquals(48.8606, result.get(0).latitude());
        assertEquals(2.3376, result.get(0).longitude());
        assertEquals(
                "entertainment.museum",
                result.get(0).category()
        );
    }

    @Test
    void shouldReturnEmptyListWhenGeoapifyReturnsNoFeatures() {

        var response = new GeoapifyResponse(
                "FeatureCollection",
                null
        );

        when(geoapifyClient.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        )).thenReturn(response);

        var result = placeService.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldSearchPlacesByDestination() {

        var location = new GeoapifyGeocodingResult(
                "Rome",
                "Rome",
                "Italy",
                "it",
                41.8933,
                12.4829,
                "Rome, Italy"
        );

        var properties = new GeoapifyProperties(
                "colosseum-test-id",
                "Colosseum",
                "Ancient Roman amphitheatre",
                41.8902,
                12.4922,
                List.of("entertainment.museum"),
                "Piazza del Colosseo, 1, 00184 Roma RM, Italy"
        );

        var feature = new GeoapifyFeature(
                "Feature",
                properties
        );

        var response = new GeoapifyResponse(
                "FeatureCollection",
                List.of(feature)
        );

        var place = new PlaceDto(
                "colosseum-test-id",
                "Colosseum",
                "Ancient Roman amphitheatre",
                41.8902,
                12.4922,
                "entertainment.museum",
                "Piazza del Colosseo, 1, 00184 Roma RM, Italy"
        );

        when(geocodingService.geocode("Rome"))
                .thenReturn(location);

        when(geoapifyClient.search(
                41.8933,
                12.4829,
                1000,
                "entertainment.museum",
                10
        )).thenReturn(response);

        when(geoapifyPlaceMapper.toPlaceDto(feature))
                .thenReturn(place);

        var result = placeService.searchByDestination(
                "Rome",
                1000,
                "entertainment.museum",
                10
        );

        assertEquals(1, result.size());
        assertEquals("Colosseum", result.get(0).name());
        assertEquals(41.8902, result.get(0).latitude());
        assertEquals(12.4922, result.get(0).longitude());
        assertEquals(
                "entertainment.museum",
                result.get(0).category()
        );
    }

    @Test
    void shouldSearchPlacesByDestinationUsingPlaceCategory() {

        var location = new GeoapifyGeocodingResult(
                "Rome",
                "Rome",
                "Italy",
                "it",
                41.8933,
                12.4829,
                "Rome, Italy"
        );

        var response = new GeoapifyResponse(
                "FeatureCollection",
                List.of()
        );

        when(geocodingService.geocode("Rome"))
                .thenReturn(location);

        when(geoapifyClient.search(
                41.8933,
                12.4829,
                1000,
                "tourism",
                10
        )).thenReturn(response);

        var result = placeService.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        );

        assertEquals(List.of(), result);
    }

    @Test
    void shouldRejectInvalidLatitude() {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        91.0,
                        2.3376,
                        1000,
                        "tourism",
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

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        181.0,
                        1000,
                        "tourism",
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

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        0,
                        "tourism",
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

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        1000,
                        "tourism",
                        101
                )
        );

        assertEquals(
                "Limit must be between 1 and 100",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankCategory() {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.search(
                        48.8606,
                        2.3376,
                        1000,
                        " ",
                        10
                )
        );

        assertEquals(
                "Category must not be blank",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectBlankDestination() {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        " ",
                        1000,
                        "tourism",
                        10
                )
        );

        assertEquals(
                "Destination must not be blank",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNullCategory() {

        var exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> placeService.searchByDestination(
                        "Rome",
                        1000,
                        (String) null,
                        10
                )
        );

        assertEquals(
                "Category must not be blank",
                exception.getMessage()
        );
    }
}