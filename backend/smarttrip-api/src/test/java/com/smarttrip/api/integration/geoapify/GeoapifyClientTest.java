package com.smarttrip.api.integration.geoapify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(GeoapifyClient.class)
@TestPropertySource(properties = "geoapify.api-key=test-api-key")
class GeoapifyClientTest {

    @Autowired
    private GeoapifyClient geoapifyClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void shouldCallGeoapifyAndDeserializeResponse() {

        String response = """
                {
                  "type": "FeatureCollection",
                  "features": [
                    {
                      "type": "Feature",
                      "properties": {
                        "name": "Musée du Louvre",
                        "description": "Un célèbre musée",
                        "lat": 48.8606,
                        "lon": 2.3376,
                        "categories": [
                          "entertainment.museum"
                        ]
                      }
                    }
                  ]
                }
                """;

        server.expect(
                        requestTo(startsWith(
                                "https://api.geoapify.com/v2/places"
                        ))
                )
                .andExpect(queryParam(
                        "categories",
                        "entertainment.museum"
                ))
                .andExpect(queryParam(
                        "filter",
                        "circle:2.3376,48.8606,1000"
                ))
                .andExpect(queryParam(
                        "bias",
                        "proximity:2.3376,48.8606"
                ))
                .andExpect(queryParam("limit", "10"))
                .andExpect(queryParam("apiKey", "test-api-key"))
                .andRespond(
                        withSuccess(
                                response,
                                MediaType.APPLICATION_JSON
                        )
                );

        GeoapifyResponse result = geoapifyClient.search(
                48.8606,
                2.3376,
                1000,
                "entertainment.museum",
                10
        );

        assertEquals("FeatureCollection", result.type());
        assertEquals(1, result.features().size());

        var feature = result.features().get(0);

        assertEquals(
                "Musée du Louvre",
                feature.properties().name()
        );

        assertEquals(
                48.8606,
                feature.properties().lat()
        );

        assertEquals(
                2.3376,
                feature.properties().lon()
        );

        assertEquals(
                "entertainment.museum",
                feature.properties().categories().get(0)
        );

        server.verify();
    }
}