package com.smarttrip.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagVectorSearchTest {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private RagDocumentService ragDocumentService;

    @Test
    void shouldFindRomeHistoryDocuments() {

        // =========================================================
        // 1. Vérifier / effectuer l'indexation
        // =========================================================

        ragDocumentService.indexDestinations();

        // =========================================================
        // 2. Recherche RAG filtrée
        // =========================================================

        String filter =
                "destination == 'Rome' && topic == 'history'";

        List<Document> documents =
                vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(
                                        "principaux lieux historiques à découvrir à Rome"
                                )
                                .topK(5)
                                .filterExpression(filter)
                                .build()
                );

        // =========================================================
        // 3. Affichage
        // =========================================================

        System.out.println(
                "\n========== RAG VECTOR SEARCH =========="
        );

        System.out.println(
                "Filtre : " + filter
        );

        System.out.println(
                "Documents trouvés : " + documents.size()
        );

        for (int i = 0; i < documents.size(); i++) {

            Document document = documents.get(i);

            System.out.println(
                    "\n--- Document " + (i + 1) + " ---"
            );

            System.out.println(
                    "ID          : " + document.getId()
            );

            System.out.println(
                    "Destination : " +
                            document.getMetadata().get("destination")
            );

            System.out.println(
                    "Topic       : " +
                            document.getMetadata().get("topic")
            );

            System.out.println(
                    "Contenu     : " +
                            document.getText()
            );
        }

        System.out.println(
                "\n=======================================\n"
        );

        // =========================================================
        // 4. Vérifications
        // =========================================================

        assertThat(documents)
                .isNotEmpty();

        assertThat(documents)
                .allSatisfy(document -> {

                    assertThat(
                            document.getMetadata()
                                    .get("destination")
                    )
                            .isEqualTo("Rome");

                    assertThat(
                            document.getMetadata()
                                    .get("topic")
                    )
                            .isEqualTo("history");
                });
    }
}