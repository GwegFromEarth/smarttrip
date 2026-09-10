package com.smarttrip.api.service;

import com.smarttrip.api.dto.RagQuery;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RagService {

    private final ChatClient ollamaChatClient;
    private final VectorStore vectorStore;
    private final RagQueryAnalyzer ragQueryAnalyzer;

    public RagService(
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient,
            VectorStore vectorStore,
            RagQueryAnalyzer ragQueryAnalyzer
    ) {
        this.ollamaChatClient = ollamaChatClient;
        this.vectorStore = vectorStore;
        this.ragQueryAnalyzer = ragQueryAnalyzer;
    }

    public String answer(String question) {

        RagQuery ragQuery =
                ragQueryAnalyzer.analyze(question);

        QuestionAnswerAdvisor advisor =
                QuestionAnswerAdvisor.builder(vectorStore)
                        .build();

        String filter =
                "destination == '%s' && topic == '%s'"
                        .formatted(
                                ragQuery.destination(),
                                ragQuery.topic()
                        );

        return ollamaChatClient
                .prompt()
                .advisors(advisor)
                .advisors(a -> a.param(
                        QuestionAnswerAdvisor.FILTER_EXPRESSION,
                        filter
                ))
                .user("""
                        Réponds uniquement à partir des informations
                        fournies par le contexte RAG.

                        Si le contexte ne contient pas suffisamment
                        d'informations pour répondre, indique-le clairement.

                        Question :
                        %s
                        """.formatted(question))
                .call()
                .content();
    }
}