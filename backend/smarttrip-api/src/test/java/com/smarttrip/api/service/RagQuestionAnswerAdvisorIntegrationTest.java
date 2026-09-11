package com.smarttrip.api.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Disabled("Test d'intégration Ollama long - à lancer manuellement")
class RagQuestionAnswerAdvisorIntegrationTest {

    @Autowired
    private RagDocumentService ragDocumentService;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    @Qualifier("ollamaChatClient")
    private ChatClient ollamaChatClient;

    @AfterEach
    void cleanUp() {
        ragDocumentService.deleteDestinations();
    }

    @Test
    void shouldAnswerUsingRagContext() {

        ragDocumentService.indexDestinations();

        QuestionAnswerAdvisor advisor =
                QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(
                                SearchRequest.builder()
                                        .topK(4)
                                        .build()
                        )
                        .build();

        var callResponse =
                ollamaChatClient
                        .prompt()
                        .advisors(advisor)
                        .advisors(a -> a.param(
                                QuestionAnswerAdvisor.FILTER_EXPRESSION,
                                "destination == 'Rome' && topic == 'history'"
                        ))
                        .user("""
                        Réponds uniquement à partir des informations
                        fournies par le contexte RAG.

                        Question :
                        Quels sont les principaux lieux historiques
                        à découvrir à Rome ?
                        """)
                        .call();

        String response = callResponse.content();

        ChatClientResponse chatClientResponse =
                callResponse.chatClientResponse();

        var retrievedDocuments =
                chatClientResponse.context()
                        .get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);

        System.out.println("\n========== RAG RESPONSE ==========");
        System.out.println(response);
        System.out.println("===================================");

        System.out.println("\n========== RAG RETRIEVED DOCUMENTS ==========");
        System.out.println(retrievedDocuments);
        System.out.println("==============================================");

        assertThat(response)
                .isNotBlank();

        System.out.println("\n========== RAG RESPONSE ==========");
        System.out.println(response);
        System.out.println("===================================\n");
    }
}