package com.smarttrip.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagVectorStoreIntegrationTest {

    @Autowired
    private VectorStore vectorStore;

    @Test
    void shouldRetrieveRelevantDocumentBySemanticSimilarity() {

        List<Document> documents = List.of(
                new Document(
                        "À Rome, le Centro Storico est particulièrement intéressant " +
                                "pour découvrir les monuments historiques, les places anciennes " +
                                "et le patrimoine de la ville.",
                        Map.of(
                                "destination", "Rome",
                                "topic", "history"
                        )
                ),
                new Document(
                        "La cuisine romaine est connue pour ses spécialités comme " +
                                "la carbonara, l'amatriciana, la cacio e pepe et les supplì.",
                        Map.of(
                                "destination", "Rome",
                                "topic", "gastronomy"
                        )
                )
        );

        vectorStore.add(documents);

        try {
            List<Document> results = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(
                                    "Quel quartier de Rome choisir pour découvrir " +
                                            "les monuments et l'histoire de la ville ?"
                            )
                            .topK(1)
                            .build()
            );

            assertThat(results).hasSize(1);
            assertThat(results.getFirst().getText())
                    .contains("Centro Storico");

        } finally {
            vectorStore.delete(
                    documents.stream()
                            .map(Document::getId)
                            .toList()
            );
        }
    }
}