package com.smarttrip.api.controller;

import com.smarttrip.api.dto.PlaceDto;
import com.smarttrip.api.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    void shouldSearchPlaces() throws Exception {

        var place = new PlaceDto(
                "louvre-test-id",
                "Louvre Museum",
                "Museum in Paris",
                48.8606,
                2.3376,
                "entertainment.museum",
                "Rue de Rivoli, 75001 Paris, France"
        );

        when(placeService.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        )).thenReturn(List.of(place));

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("radius", "1000")
                                .param("category", "entertainment.museum")
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
                        .value("entertainment.museum"));

        verify(placeService).search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        );
    }

    @Test
    void shouldUseDefaultRadiusAndLimit() throws Exception {

        when(placeService.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/places")
                                .param("latitude", "48.8606")
                                .param("longitude", "2.3376")
                                .param("category", "entertainment.museum")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(placeService).search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
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
                "tourism.attraction",
                "Piazza del Colosseo, 1, 00184 Roma RM, Italy"
        );

        when(placeService.searchByDestination(
                "Rome",
                1000,
                "tourism.attraction",
                10
        )).thenReturn(List.of(place));

        mockMvc.perform(
                        get("/api/places/by-destination")
                                .param("destination", "Rome")
                                .param("radius", "1000")
                                .param("category", "tourism.attraction")
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
                        .value("tourism.attraction"));
    }

    @Test
    void shouldUseDefaultRadiusAndLimitForDestination() throws Exception {

        when(placeService.searchByDestination(
                "Rome",
                1000,
                "entertainment.museum",
                10
        )).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/places/by-destination")
                                .param("destination", "Rome")
                                .param("category", "entertainment.museum")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}