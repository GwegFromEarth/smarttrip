package com.smarttrip.api.service;

import com.smarttrip.api.dto.RagQuery;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Disabled("Test d'intégration Ollama long - à lancer manuellement")
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

    @Autowired
    private RagQueryAnalyzer ragQueryAnalyzer;

    @Test
    void shouldAnswerUsingRagAndTools() {

        long totalStart = System.currentTimeMillis();

        // =========================================================
        // 1. INDEXATION RAG
        // =========================================================

        long indexingStart = System.currentTimeMillis();

        ragDocumentService.indexDestinations();

        long indexingDuration =
                System.currentTimeMillis() - indexingStart;

        // =========================================================
        // 2. ANALYSE DE LA QUESTION
        // =========================================================

        String question =
                "Quels sont les principaux lieux historiques " +
                        "à découvrir à Rome ?";

        RagQuery ragQuery =
                ragQueryAnalyzer.analyze(question);

        System.out.println(
                "\n========== RAG QUERY =========="
        );

        System.out.println(
                "destination = " + ragQuery.destination()
        );

        System.out.println(
                "topic       = " + ragQuery.topic()
        );

        System.out.println(
                "================================\n"
        );

        // =========================================================
        // 3. CONSEILLER RAG
        // =========================================================

        QuestionAnswerAdvisor advisor =
                QuestionAnswerAdvisor
                        .builder(vectorStore)
                        .build();

        // =========================================================
        // 4. APPEL OLLAMA + RAG + TOOLS
        // =========================================================

        long aiStart = System.currentTimeMillis();

        String response =
                ollamaChatClient
                        .prompt()
                        .advisors(advisor)
                        .advisors(a -> a.param(
                                QuestionAnswerAdvisor.FILTER_EXPRESSION,
                                "destination == '%s' && topic == '%s'"
                                        .formatted(
                                                ragQuery.destination(),
                                                ragQuery.topic()
                                        )
                        ))
                        .tools(placeTools)
                        .user("""
                            Tu es SmartTrip, un assistant de voyage.

                            Réponds à la question de l'utilisateur
                            uniquement à partir des informations
                            disponibles dans le contexte RAG et,
                            lorsque cela est pertinent, des résultats
                            fournis par les outils.

                            Lorsque la question demande des lieux
                            touristiques précis à visiter, utilise
                            l'outil de recherche de lieux touristiques.

                            Ne prétends pas avoir utilisé un outil
                            si tu ne l'as pas utilisé.

                            Réponds en français, de manière claire
                            et concise.

                            QUESTION :
                            %s
                            """.formatted(question))
                        .call()
                        .content();

        long aiDuration =
                System.currentTimeMillis() - aiStart;

        // =========================================================
        // 5. AFFICHAGE
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

        System.out.println(
                "\n========== PERFORMANCE =========="
        );

        System.out.printf(
                "Indexation RAG        : %.2f s%n",
                indexingDuration / 1000.0
        );

        System.out.printf(
                "Ollama + RAG + Tools  : %.2f s%n",
                aiDuration / 1000.0
        );

        System.out.printf(
                "TOTAL                  : %.2f s%n",
                totalDuration / 1000.0
        );

        System.out.println(
                "=================================\n"
        );

        // =========================================================
        // 6. VÉRIFICATIONS
        // =========================================================

        assertThat(ragQuery.destination())
                .isEqualToIgnoringCase("Rome");

        assertThat(ragQuery.topic())
                .isEqualTo("history");

        assertThat(response)
                .isNotBlank();
    }

}
