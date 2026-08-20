package com.smarttrip.api.integration.overpass;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.POST;

@RestClientTest(OverpassClient.class)
class OverpassClientTest {

    @Autowired
    private OverpassClient overpassClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void shouldCallOverpassAndDeserializeResponse() {

        String responseJson = """
                {
                  "version": 0.6,
                  "elements": [
                    {
                      "type": "node",
                      "id": 123,
                      "lat": 41.9019689,
                      "lon": 12.4907414,
                      "tags": {
                        "name": "Quattro Fontane",
                        "tourism": "attraction"
                      }
                    }
                  ]
                }
                """;

        String query = """
                [out:json];
                node["tourism"="attraction"](41.85,12.40,41.95,12.60);
                out center;
                """;

        server.expect(requestTo("https://overpass-api.de/api/interpreter"))
                .andExpect(method(POST))
                .andExpect(content().string(query))
                .andRespond(
                        withSuccess(responseJson, MediaType.APPLICATION_JSON)
                );

        OverpassResponse result = overpassClient.search(query);

        assertThat(result).isNotNull();
        assertThat(result.elements()).hasSize(1);

        var element = result.elements().getFirst();

        assertThat(element.type()).isEqualTo("node");
        assertThat(element.id()).isEqualTo(123L);
        assertThat(element.lat()).isEqualTo(41.9019689);
        assertThat(element.lon()).isEqualTo(12.4907414);
        assertThat(element.tags().get("name"))
                .isEqualTo("Quattro Fontane");

        server.verify();
    }
}