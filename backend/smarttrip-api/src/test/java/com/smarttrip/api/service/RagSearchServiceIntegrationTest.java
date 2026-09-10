package com.smarttrip.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagSearchServiceIntegrationTest {

    @Autowired
    private RagDocumentService ragDocumentService;

    @Autowired
    private RagSearchService ragSearchService;

    @Autowired
    private VectorStore vectorStore;

    @AfterEach
    void cleanUp() {
        ragDocumentService.deleteDestinations();
    }

    @Test
    void shouldSearchRelevantDestinationDocuments() {

        ragDocumentService.indexDestinations();

        List<Document> results =
                ragSearchService.search(
                        "Quels lieux permettent de découvrir l'histoire de Rome ?"
                );

        assertThat(results)
                .isNotEmpty();

        assertThat(results)
                .anySatisfy(document -> {
                    assertThat(document.getMetadata())
                            .containsEntry(
                                    "destination",
                                    "Rome"
                            );

                    assertThat(document.getMetadata())
                            .containsEntry(
                                    "topic",
                                    "history"
                            );
                });
    }

    @Test
    void shouldFilterSearchByDestination() {

        ragDocumentService.indexDestinations();

        List<Document> results =
                ragSearchService.search(
                        "Quels sont les lieux historiques ?",
                        "Rome"
                );

        assertThat(results)
                .isNotEmpty();

        assertThat(results)
                .allSatisfy(document ->
                        assertThat(
                                document.getMetadata()
                                        .get("destination")
                        )
                                .isEqualTo("Rome")
                );
    }
}