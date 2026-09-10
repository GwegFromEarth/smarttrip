package com.smarttrip.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagContextServiceIntegrationTest {

    @Autowired
    private RagDocumentService ragDocumentService;

    @Autowired
    private RagContextService ragContextService;

    @AfterEach
    void cleanUp() {
        ragDocumentService.deleteDestinations();
    }

    @Test
    void shouldBuildContextFromRelevantDocuments() {

        ragDocumentService.indexDestinations();

        String context = ragContextService.buildContext(
                "Quels sont les lieux historiques à découvrir à Rome ?",
                "Rome"
        );

        assertThat(context)
                .isNotBlank();

        assertThat(context)
                .contains("Rome");

        assertThat(context)
                .contains("Colisée");

        assertThat(context)
                .contains("Forum romain");
    }
}