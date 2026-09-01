package com.smarttrip.api.integration.foursquare;

import com.smarttrip.api.dto.PlaceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FoursquarePlaceServiceCacheTest {

    @MockitoBean
    private FoursquareClient foursquareClient;

    @Autowired
    private FoursquarePlaceService service;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCache() {
        Cache cache = cacheManager.getCache("foursquarePlaces");

        assertNotNull(cache);

        cache.clear();
    }

    @Test
    void shouldReuseCachedResultsForIdenticalSearch() {

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

        List<?> firstResult = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        List<?> secondResult = service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(firstResult);
        assertNotNull(secondResult);

        assertSame(firstResult, secondResult);

        verify(foursquareClient, times(1)).search(
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
    void shouldUseDifferentCacheEntryForDifferentCategory() {

        FoursquareResponse museumResponse =
                new FoursquareResponse(List.of());

        FoursquareResponse restaurantResponse =
                new FoursquareResponse(List.of());

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                "RATING",
                10
        )).thenReturn(museumResponse);

        when(foursquareClient.search(
                41.8902,
                12.4922,
                1000,
                "restaurant",
                null,
                "RATING",
                10
        )).thenReturn(restaurantResponse);

        service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        service.searchPlaces(
                41.8902,
                12.4922,
                1000,
                PlaceCategory.RESTAURANT,
                10
        );

        verify(foursquareClient, times(1)).search(
                41.8902,
                12.4922,
                1000,
                "museum",
                null,
                "RATING",
                10
        );

        verify(foursquareClient, times(1)).search(
                41.8902,
                12.4922,
                1000,
                "restaurant",
                null,
                "RATING",
                10
        );
    }

    @Test
    void shouldReuseCachedResultsForIdenticalDestinationSearch() {

        FoursquareResponse response =
                new FoursquareResponse(List.of());

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(response);

        List<?> firstResult = service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        List<?> secondResult = service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        assertNotNull(firstResult);
        assertNotNull(secondResult);

        assertSame(firstResult, secondResult);

        verify(foursquareClient, times(1)).searchByDestination(
                "Rome",
                "museum",
                null,
                10
        );
    }

    @Test
    void shouldUseDifferentCacheEntryForDifferentDestinations() {

        FoursquareResponse romeResponse =
                new FoursquareResponse(List.of());

        FoursquareResponse parisResponse =
                new FoursquareResponse(List.of());

        when(foursquareClient.searchByDestination(
                "Rome",
                "museum",
                null,
                10
        )).thenReturn(romeResponse);

        when(foursquareClient.searchByDestination(
                "Paris",
                "museum",
                null,
                10
        )).thenReturn(parisResponse);

        service.searchByDestination(
                "Rome",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        service.searchByDestination(
                "Paris",
                1000,
                PlaceCategory.MUSEUM,
                10
        );

        verify(foursquareClient, times(1)).searchByDestination(
                "Rome",
                "museum",
                null,
                10
        );

        verify(foursquareClient, times(1)).searchByDestination(
                "Paris",
                "museum",
                null,
                10
        );
    }
}