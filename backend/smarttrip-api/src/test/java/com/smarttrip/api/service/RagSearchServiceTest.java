package com.smarttrip.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagSearchServiceTest {

    @Autowired
    private RagSearchService ragSearchService;

    @Autowired
    private RagDocumentService ragDocumentService;

    @BeforeEach
    void setUp() {
        ragDocumentService.indexDestinations();
    }

    @Test
    void shouldSearchDocumentsWithoutFilter() {

        List<Document> documents =
                ragSearchService.search(
                        "principaux lieux historiques à découvrir"
                );

        assertThat(documents)
                .isNotEmpty();

        System.out.println(
                "\n========== RAG SEARCH =========="
        );

        System.out.println(
                "Mode : recherche générale"
        );

        System.out.println(
                "Documents trouvés : " +
                        documents.size()
        );

        documents.forEach(document ->
                System.out.println(
                        "\n--- Document ---\n" +
                                document.getText()
                )
        );

        System.out.println(
                "================================\n"
        );
    }

    @Test
    void shouldSearchDocumentsForDestination() {

        List<Document> documents =
                ragSearchService.search(
                        "principaux lieux historiques à découvrir",
                        "Rome"
                );

        assertThat(documents)
                .isNotEmpty();

        assertThat(documents)
                .allMatch(document ->
                        "Rome".equals(
                                document.getMetadata()
                                        .get("destination")
                        )
                );

        System.out.println(
                "\n========== RAG SEARCH =========="
        );

        System.out.println(
                "Mode : destination = Rome"
        );

        System.out.println(
                "Documents trouvés : " +
                        documents.size()
        );

        documents.forEach(document ->
                System.out.println(
                        "\n--- Document ---\n" +
                                document.getText()
                )
        );

        System.out.println(
                "================================\n"
        );
    }

    @Test
    void shouldSearchDocumentsForDestinationAndTopic() {

        List<Document> documents =
                ragSearchService.search(
                        "principaux lieux historiques à découvrir",
                        "Rome",
                        "history"
                );

        assertThat(documents)
                .isNotEmpty();

        assertThat(documents)
                .allMatch(document ->
                        "Rome".equals(
                                document.getMetadata()
                                        .get("destination")
                        )
                );

        assertThat(documents)
                .allMatch(document ->
                        "history".equals(
                                document.getMetadata()
                                        .get("topic")
                        )
                );

        System.out.println(
                "\n========== RAG SEARCH =========="
        );

        System.out.println(
                "Mode : destination = Rome + topic = history"
        );

        System.out.println(
                "Documents trouvés : " +
                        documents.size()
        );

        documents.forEach(document ->
                System.out.println(
                        "\n--- Document ---\n" +
                                document.getText()
                )
        );

        System.out.println(
                "================================\n"
        );
    }
}