package com.smarttrip.api.service;

import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.integration.geoapify.GeoapifyClient;
import com.smarttrip.api.integration.geoapify.GeoapifyFeature;
import com.smarttrip.api.integration.geoapify.GeoapifyPlaceMapper;
import com.smarttrip.api.integration.geoapify.GeoapifyProperties;
import com.smarttrip.api.integration.geoapify.GeoapifyResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlaceServiceTest {

    private final GeoapifyClient geoapifyClient = mock(GeoapifyClient.class);
    private final GeoapifyPlaceMapper geoapifyPlaceMapper =
            mock(GeoapifyPlaceMapper.class);

    private final PlaceService placeService =
            new PlaceService(geoapifyClient, geoapifyPlaceMapper);

    @Test
    void shouldSearchPlaces() {

        var properties = new GeoapifyProperties(
                "Louvre Museum",
                "Museum in Paris",
                48.8606,
                2.3376,
                List.of("entertainment.museum")
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
                "Louvre Museum",
                "Museum in Paris",
                48.8606,
                2.3376,
                "entertainment.museum"
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
}