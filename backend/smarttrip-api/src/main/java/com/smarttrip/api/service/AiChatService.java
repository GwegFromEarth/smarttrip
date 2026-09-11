package com.smarttrip.api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiChatService {

    private final ChatClient geminiChatClient;
    private final ChatClient ollamaChatClient;

    public AiChatService(
            @Qualifier("geminiChatClient") ChatClient geminiChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient
    ) {
        this.geminiChatClient = geminiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String generateResponse(
            String message,
            Object... tools
    ) {

        try {
            return geminiChatClient
                    .prompt()
                    .user(message)
                    .tools(tools)
                    .call()
                    .content();

        } catch (Exception e) {

            return ollamaChatClient
                    .prompt()
                    .user(message)
                    .tools(tools)
                    .call()
                    .content();
        }
    }

    public Flux<String> streamResponse(
            List<Message> messages,
            Object... tools
    ) {
        return streamResponse(
                messages,
                "",
                tools
        );
    }

    public Flux<String> streamResponse(
            List<Message> messages,
            String ragContext,
            Object... tools
    ) {

        List<Message> messagesWithRagContext =
                addRagContext(
                        messages,
                        ragContext
                );

        return geminiChatClient
                .prompt()
                .messages(messagesWithRagContext)
                .tools(tools)
                .stream()
                .content()
                .onErrorResume(
                        error -> ollamaChatClient
                                .prompt()
                                .messages(messagesWithRagContext)
                                .tools(tools)
                                .stream()
                                .content()
                );
    }

    private List<Message> addRagContext(
            List<Message> messages,
            String ragContext
    ) {

        if (ragContext == null || ragContext.isBlank()) {
            return messages;
        }

        List<Message> enrichedMessages =
                new ArrayList<>(messages);

        String contextMessage = """
                CONTEXTE RAG SMARTTRIP

                Les informations suivantes proviennent de la base
                documentaire de SmartTrip.

                Utilise-les lorsqu'elles sont pertinentes pour répondre
                à la question de l'utilisateur.

                N'invente pas d'informations qui ne sont pas présentes
                dans le contexte lorsque celui-ci fournit une réponse.

                Contexte :
                %s
                """.formatted(ragContext);

        enrichedMessages.add(
                new SystemMessage(contextMessage)
        );

        return enrichedMessages;
    }

    public <T> T generateEntity(
            String message,
            Class<T> responseType,
            Object... tools
    ) {

        try {
            return geminiChatClient
                    .prompt()
                    .user(message)
                    .tools(tools)
                    .call()
                    .entity(
                            responseType,
                            spec -> spec
                                    .useProviderStructuredOutput()
                                    .validateSchema()
                    );

        } catch (Exception e) {

            return ollamaChatClient
                    .prompt()
                    .user(message)
                    .tools(tools)
                    .call()
                    .entity(
                            responseType,
                            spec -> spec
                                    .validateSchema()
                    );
        }
    }
}