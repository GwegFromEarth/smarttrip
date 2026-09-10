package com.smarttrip.api.service;

import com.smarttrip.api.dto.RagQuery;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RagQueryAnalyzer {

    private final ChatClient ollamaChatClient;

    public RagQueryAnalyzer(
            @Qualifier("ollamaChatClient")
            ChatClient ollamaChatClient
    ) {
        this.ollamaChatClient = ollamaChatClient;
    }

    public RagQuery analyze(String question) {

        return ollamaChatClient
                .prompt()
                .user("""
                        Analyse la question de voyage suivante.

                        Extrais :
                        - destination : la ville concernée
                        - topic : le thème principal de la question

                        Les valeurs possibles pour topic sont :
                        - history
                        - gastronomy
                        - neighborhoods
                        - practical

                        Si le thème ne correspond pas exactement à une
                        valeur possible, choisis la valeur la plus proche.

                        Question :
                        %s
                        """.formatted(question))
                .call()
                .entity(
                        RagQuery.class,
                        spec -> spec.validateSchema()
                );
    }
}