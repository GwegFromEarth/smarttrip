package com.smarttrip.api.service;

import com.smarttrip.api.integration.geoapify.GeoapifyGeocodingClient;
import com.smarttrip.api.integration.geoapify.GeoapifyGeocodingResponse;
import com.smarttrip.api.integration.geoapify.GeoapifyGeocodingResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class GeocodingServiceTest {

    @Autowired
    private GeocodingService geocodingService;

    @MockitoBean
    private GeoapifyGeocodingClient geocodingClient;

    @Test
    void shouldReturnFirstGeocodingResult() {

        var result = new GeoapifyGeocodingResult(
                "Rome",
                "Rome",
                "Italy",
                "it",
                41.8933,
                12.4829,
                "Rome, Italy"
        );

        when(geocodingClient.searchCity("Rome"))
                .thenReturn(
                        new GeoapifyGeocodingResponse(
                                List.of(result)
                        )
                );

        var actual = geocodingService.geocode("Rome");

        assertThat(actual).isSameAs(result);
        assertThat(actual.name()).isEqualTo("Rome");
        assertThat(actual.city()).isEqualTo("Rome");
        assertThat(actual.country()).isEqualTo("Italy");
        assertThat(actual.countryCode()).isEqualTo("it");
        assertThat(actual.lat()).isEqualTo(41.8933);
        assertThat(actual.lon()).isEqualTo(12.4829);
    }

    @Test
    void shouldThrowExceptionWhenDestinationIsNotFound() {

        when(geocodingClient.searchCity("Atlantis"))
                .thenReturn(
                        new GeoapifyGeocodingResponse(
                                List.of()
                        )
                );

        assertThat(
                org.assertj.core.api.Assertions.catchThrowable(
                        () -> geocodingService.geocode("Atlantis")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Destination introuvable : Atlantis");
    }
}