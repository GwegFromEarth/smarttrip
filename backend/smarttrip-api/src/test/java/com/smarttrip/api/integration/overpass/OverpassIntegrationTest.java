package com.smarttrip.api.integration.overpass;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

class OverpassIntegrationTest {

    @Test
    void shouldCallRealOverpassApi() {

        RestClient restClient = RestClient.builder()
                .baseUrl("https://overpass-api.de")
                .build();

        String query = """
        [out:json][timeout:5];
        node(41.899,12.490,41.900,12.491);
        out;
        """;

        OverpassResponse result = restClient
                .post()
                .uri("/api/interpreter")
                .body(query)
                .retrieve()
                .body(OverpassResponse.class);

        assertThat(result).isNotNull();
        assertThat(result.elements()).isNotEmpty();

        result.elements().forEach(element -> {
            String name = element.tags() != null
                    ? element.tags().get("name")
                    : null;

            System.out.println(
                    (name != null ? name : "<sans nom>")
                            + " [" + element.type() + "]"
            );
        });
    }
}