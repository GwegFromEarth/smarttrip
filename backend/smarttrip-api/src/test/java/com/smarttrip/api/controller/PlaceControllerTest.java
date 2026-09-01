package com.smarttrip.api.controller;

import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.service.PlaceService;
import com.smarttrip.api.dto.PlaceCategory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    @TestConfiguration
    static class TestCacheConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Test
    void shouldSearchPlaces() throws Exception {

        var place = new PlaceDto(
                "louvre-test-id",
                "Louvre Museum",
                "Museum in Paris",
                48.8606,
                2.3376,
                PlaceCategory.MUSEUM,
                "Rue de Rivoli, 75001 Paris, France",
                null,
                null,
                null
        );

        when(placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        )).thenReturn(List.of(place));

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("radius", "1000")
                                .param("category", "MUSEUM")
                                .param("limit", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$[0].name")
                        .value("Louvre Museum"))
                .andExpect(jsonPath("$[0].latitude")
                        .value(48.8606))
                .andExpect(jsonPath("$[0].longitude")
                        .value(2.3376))
                .andExpect(jsonPath("$[0].category")
                        .value("MUSEUM"));

        verify(placeService).search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        );
    }

    @Test
    void shouldUseDefaultRadiusAndLimit() throws Exception {

        when(placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("category", "MUSEUM")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(placeService).search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.MUSEUM,
                10
        );
    }

    @Test
    void shouldSearchPlacesByDestination() throws Exception {

        var place = new PlaceDto(
                "colosseum-test-id",
                "Colosseum",
                "Ancient Roman amphitheatre",
                41.8902,
                12.4922,
                PlaceCategory.TOURIST_ATTRACTION,
                "Piazza del Colosseo, 1, 00184 Roma RM, Italy",
                null,
                null,
                null
        );

        when(placeService.searchByDestination(
                "Rome",
                PlaceCategory.TOURIST_ATTRACTION,
                10
        )).thenReturn(List.of(place));

        mockMvc.perform(
                        get("/api/places/by-destination")
                                .param("destination", "Rome")
                                .param("radius", "1000")
                                .param("category", "TOURIST_ATTRACTION")
                                .param("limit", "10")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/json"
                ))
                .andExpect(jsonPath("$[0].name")
                        .value("Colosseum"))
                .andExpect(jsonPath("$[0].latitude")
                        .value(41.8902))
                .andExpect(jsonPath("$[0].longitude")
                        .value(12.4922))
                .andExpect(jsonPath("$[0].category")
                        .value("TOURIST_ATTRACTION"));
    }

    @Test
    void shouldUseDefaultRadiusAndLimitForDestination() throws Exception {

        when(placeService.searchByDestination(
                "Rome",
                PlaceCategory.MUSEUM,
                10
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/places/by-destination")
                                .param("destination", "Rome")
                                .param("category", "MUSEUM")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnBadRequestWhenRadiusIsInvalid() throws Exception {

        when(placeService.search(
                48.8606,
                2.3376,
                0,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        )).thenThrow(
                new IllegalArgumentException(
                        "Radius must be at least 1 meter"
                )
        );

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("radius", "0")
                                .param("category", "TOURIST_ATTRACTION")
                                .param("limit", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid request"))
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Radius must be at least 1 meter"));
    }

    @Test
    void shouldReturnBadRequestWhenLimitIsInvalid() throws Exception {

        when(placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.TOURIST_ATTRACTION,
                101
        )).thenThrow(
                new IllegalArgumentException(
                        "Limit must be between 1 and 100"
                )
        );

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("radius", "1000")
                                .param("category", "TOURIST_ATTRACTION")
                                .param("limit", "101")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid request"))
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Limit must be between 1 and 100"));
    }

    @Test
    void shouldReturnBadRequestWithMessageWhenRadiusIsInvalid()
            throws Exception {

        when(placeService.search(
                48.8606,
                2.3376,
                0,
                PlaceCategory.TOURIST_ATTRACTION,
                10
        )).thenThrow(
                new IllegalArgumentException(
                        "Radius must be at least 1 meter"
                )
        );

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("radius", "0")
                                .param("category", "TOURIST_ATTRACTION")
                                .param("limit", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.detail")
                        .value("Radius must be at least 1 meter"));
    }

    @Test
    void shouldReturnBadRequestWithMessageWhenLimitIsInvalid()
            throws Exception {

        when(placeService.search(
                48.8606,
                2.3376,
                1000,
                PlaceCategory.TOURIST_ATTRACTION,
                101
        )).thenThrow(
                new IllegalArgumentException(
                        "Limit must be between 1 and 100"
                )
        );

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("radius", "1000")
                                .param("category", "TOURIST_ATTRACTION")
                                .param("limit", "101")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid request"))
                .andExpect(jsonPath("$.detail")
                        .value("Limit must be between 1 and 100"));
    }

    @Test
    void shouldReturnBadRequestWhenDestinationIsNull()
            throws Exception {

        mockMvc.perform(
                        get("/api/places/by-destination")
                                .param("radius", "1000")
                                .param("category", "TOURIST_ATTRACTION")
                                .param("limit", "10")
                )
                .andExpect(status().isBadRequest());
    }
}