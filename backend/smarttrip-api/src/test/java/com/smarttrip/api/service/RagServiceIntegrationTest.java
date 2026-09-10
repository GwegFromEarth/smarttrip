package com.smarttrip.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RagServiceIntegrationTest {

    @Autowired
    private RagDocumentService ragDocumentService;

    @Autowired
    private RagService ragService;

    @AfterEach
    void cleanUp() {
        ragDocumentService.deleteDestinations();
    }

    @Test
    void shouldAnswerUsingFilteredRagContext() {

        ragDocumentService.indexDestinations();

        String response =
                ragService.answer(
                        "Quels sont les principaux lieux historiques à découvrir à Rome ?"
                );

        System.out.println("\n========== RAG SERVICE RESPONSE ==========");
        System.out.println(response);
        System.out.println("==========================================\n");

        assertThat(response)
                .isNotBlank();
    }
}