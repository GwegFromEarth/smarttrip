package com.smarttrip.api.service;

import com.smarttrip.api.dto.RagQuery;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Disabled("Test d'intégration Ollama long - à lancer manuellement")
class RagQueryAnalyzerIntegrationTest {

    @Autowired
    private RagQueryAnalyzer ragQueryAnalyzer;

    @Test
    void shouldExtractDestinationAndTopic() {

        RagQuery result =
                ragQueryAnalyzer.analyze(
                        "Quels sont les principaux lieux historiques à découvrir à Rome ?"
                );

        System.out.println("\n========== RAG QUERY ANALYSIS ==========");
        System.out.println("ragRelevant = " + result.ragRelevant());
        System.out.println("destination = " + result.destination());
        System.out.println("topic       = " + result.topic());
        System.out.println("=========================================\n");

        assertThat(result.ragRelevant())
                .isTrue();

        assertThat(result.destination())
                .isEqualToIgnoringCase("Rome");

        assertThat(result.topic())
                .isEqualTo("history");
    }

    @Test
    void shouldDetectQuestionNotRelevantToRag() {

        RagQuery result =
                ragQueryAnalyzer.analyze(
                        "Quel temps fera-t-il à Rome demain ?"
                );

        System.out.println("\n========== RAG QUERY ANALYSIS ==========");
        System.out.println("ragRelevant = " + result.ragRelevant());
        System.out.println("destination = " + result.destination());
        System.out.println("topic       = " + result.topic());
        System.out.println("=========================================\n");

        assertThat(result.ragRelevant())
                .isFalse();
    }
}