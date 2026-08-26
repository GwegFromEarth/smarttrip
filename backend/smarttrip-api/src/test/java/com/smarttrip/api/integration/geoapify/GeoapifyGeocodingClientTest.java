package com.smarttrip.api.integration.geoapify;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GeoapifyGeocodingClient.class)
@TestPropertySource(properties = "geoapify.api-key=test-api-key")
class GeoapifyGeocodingClientTest {

    @Autowired
    private GeoapifyGeocodingClient geocodingClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void shouldCallGeoapifyGeocodingAndDeserializeResponse() {

        server.expect(
                        requestTo(Matchers.startsWith(
                                "https://api.geoapify.com/v1/geocode/search"
                        ))
                )
                .andExpect(queryParam("text", "Rome"))
                .andExpect(queryParam("type", "city"))
                .andExpect(queryParam("limit", "1"))
                .andExpect(queryParam("format", "json"))
                .andExpect(queryParam("apiKey", "test-api-key"))
                .andRespond(
                        withSuccess(
                                """
                                {
                                  "results": [
                                    {
                                      "name": "Rome",
                                      "city": "Rome",
                                      "country": "Italy",
                                      "country_code": "it",
                                      "lat": 41.8933,
                                      "lon": 12.4829,
                                      "formatted": "Rome, Italy"
                                    }
                                  ]
                                }
                                """,
                                MediaType.APPLICATION_JSON
                        )
                );

        GeoapifyGeocodingResponse result =
                geocodingClient.searchCity("Rome");

        assertEquals(1, result.results().size());

        var city = result.results().get(0);

        assertEquals("Rome", city.name());
        assertEquals("Rome", city.city());
        assertEquals("Italy", city.country());
        assertEquals("it", city.countryCode());
        assertEquals(41.8933, city.lat());
        assertEquals(12.4829, city.lon());

        server.verify();
    }
}