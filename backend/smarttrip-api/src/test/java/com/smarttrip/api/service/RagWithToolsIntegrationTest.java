package com.smarttrip.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagWithToolsIntegrationTest {

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private RagDocumentService ragDocumentService;

    @Autowired
    private PlaceTools placeTools;

    @Test
    void shouldAnswerUsingRagAndTools() {

        long totalStart = System.currentTimeMillis();

        // =========================================================
        // 1. Indexation des données RAG
        // =========================================================

        long indexingStart = System.currentTimeMillis();

        ragDocumentService.indexDestinations();

        long indexingDuration =
                System.currentTimeMillis() - indexingStart;

        // =========================================================
        // 2. Construction du conseiller RAG
        // =========================================================

        QuestionAnswerAdvisor advisor =
                QuestionAnswerAdvisor.builder(vectorStore)
                        .build();

        // =========================================================
        // 3. Appel Ollama + RAG + Tools
        // =========================================================

        long aiStart = System.currentTimeMillis();

        String response =
                ollamaChatClient
                        .prompt()
                        .advisors(advisor)
                        .tools(placeTools)
                        .user("""
                                Réponds à la question de l'utilisateur
                                en utilisant les informations pertinentes
                                du contexte RAG.

                                Lorsque la question demande des lieux
                                touristiques précis à visiter, utilise
                                l'outil de recherche de lieux touristiques.

                                Question :
                                Quels sont les principaux lieux historiques
                                à découvrir à Rome ?
                                """)
                        .call()
                        .content();

        long aiDuration =
                System.currentTimeMillis() - aiStart;

        // =========================================================
        // 4. Affichage du résultat
        // =========================================================

        long totalDuration =
                System.currentTimeMillis() - totalStart;

        System.out.println(
                "\n========== RAG + TOOLS RESPONSE =========="
        );

        System.out.println(response);

        System.out.println(
                "==========================================="
        );

        // =========================================================
        // 5. Mesures de performance
        // =========================================================

        System.out.println(
                "\n========== PERFORMANCE =========="
        );

        System.out.printf(
                "Indexation RAG       : %.2f s%n",
                indexingDuration / 1000.0
        );

        System.out.printf(
                "Ollama + RAG + Tools : %.2f s%n",
                aiDuration / 1000.0
        );

        System.out.printf(
                "TOTAL                 : %.2f s%n",
                totalDuration / 1000.0
        );

        System.out.println(
                "=================================\n"
        );

        // =========================================================
        // 6. Vérifications
        // =========================================================

        assertThat(response)
                .isNotBlank();
    }
}