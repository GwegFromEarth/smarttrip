package com.smarttrip.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagDocumentServiceIntegrationTest {

    private static final String SOURCE = "smarttrip-destinations";

    @Autowired
    private RagDocumentService ragDocumentService;

    @Autowired
    private VectorStore vectorStore;

    @AfterEach
    void cleanUp() {
        ragDocumentService.deleteDestinations();
    }

    @Test
    void shouldIndexDestinationDocumentsAndSearchThem() {

        int indexedDocuments =
                ragDocumentService.indexDestinations();

        assertThat(indexedDocuments)
                .isEqualTo(6);

        List<Document> results =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query("Quels lieux permettent de découvrir l'histoire de Rome ?")
                                .topK(3)
                                .filterExpression(
                                        "source == '" + SOURCE + "'"
                                )
                                .build()
                );

        assertThat(results)
                .isNotEmpty();

        assertThat(results)
                .allSatisfy(document -> {

                    assertThat(document.getMetadata())
                            .containsEntry(
                                    "source",
                                    SOURCE
                            );

                    assertThat(document.getMetadata())
                            .containsKey("destination");

                    assertThat(document.getMetadata())
                            .containsKey("topic");
                });

        assertThat(
                results.stream()
                        .map(document ->
                                document.getMetadata()
                                        .get("destination"))
        )
                .contains("Rome");

        assertThat(
                results.stream()
                        .map(document ->
                                document.getMetadata()
                                        .get("topic"))
        )
                .contains("history");
    }
}