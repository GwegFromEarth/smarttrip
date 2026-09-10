package com.smarttrip.api.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class AiChatService {

    private final ChatClient geminiChatClient;
    private final ChatClient ollamaChatClient;

    public AiChatService(
            ChatClient geminiChatClient,
            ChatClient ollamaChatClient
    ) {
        this.geminiChatClient = geminiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String generateResponse(String message, Object... tools) {

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
        return geminiChatClient
                .prompt()
                .messages(messages)
                .tools(tools)
                .stream()
                .content()
                .onErrorResume(
                        error -> ollamaChatClient
                                .prompt()
                                .messages(messages)
                                .tools(tools)
                                .stream()
                                .content()
                );
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