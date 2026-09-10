package com.smarttrip.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AiChatServiceTest {

    private final ChatClient geminiChatClient =
            mock(ChatClient.class);

    private final ChatClient ollamaChatClient =
            mock(ChatClient.class);

    private final ChatClient.ChatClientRequestSpec geminiRequest =
            mock(ChatClient.ChatClientRequestSpec.class);

    private final ChatClient.ChatClientRequestSpec ollamaRequest =
            mock(ChatClient.ChatClientRequestSpec.class);

    private final ChatClient.CallResponseSpec geminiResponse =
            mock(ChatClient.CallResponseSpec.class);

    private final ChatClient.CallResponseSpec ollamaResponse =
            mock(ChatClient.CallResponseSpec.class);

    private final ChatClient.StreamResponseSpec geminiStreamResponse =
            mock(ChatClient.StreamResponseSpec.class);

    private final ChatClient.StreamResponseSpec ollamaStreamResponse =
            mock(ChatClient.StreamResponseSpec.class);

    private final AiChatService service =
            new AiChatService(
                    geminiChatClient,
                    ollamaChatClient
            );

    @Test
    void shouldUseGeminiForGenerateResponse() {

        when(geminiChatClient.prompt())
                .thenReturn(geminiRequest);

        when(geminiRequest.user("Bonjour"))
                .thenReturn(geminiRequest);

        when(geminiRequest.tools())
                .thenReturn(geminiRequest);

        when(geminiRequest.call())
                .thenReturn(geminiResponse);

        when(geminiResponse.content())
                .thenReturn("Bonjour !");

        String result =
                service.generateResponse("Bonjour");

        assertEquals("Bonjour !", result);

        verify(geminiChatClient).prompt();
        verify(geminiRequest).user("Bonjour");
        verify(geminiRequest).tools();
        verify(geminiRequest).call();
        verify(geminiResponse).content();

        verifyNoInteractions(ollamaChatClient);
    }

    @Test
    void shouldFallbackToOllamaWhenGeminiFails() {

        when(geminiChatClient.prompt())
                .thenReturn(geminiRequest);

        when(geminiRequest.user("Bonjour"))
                .thenReturn(geminiRequest);

        when(geminiRequest.tools())
                .thenReturn(geminiRequest);

        when(geminiRequest.call())
                .thenThrow(new RuntimeException("Gemini indisponible"));

        when(ollamaChatClient.prompt())
                .thenReturn(ollamaRequest);

        when(ollamaRequest.user("Bonjour"))
                .thenReturn(ollamaRequest);

        when(ollamaRequest.tools())
                .thenReturn(ollamaRequest);

        when(ollamaRequest.call())
                .thenReturn(ollamaResponse);

        when(ollamaResponse.content())
                .thenReturn("Réponse Ollama");

        String result =
                service.generateResponse("Bonjour");

        assertEquals("Réponse Ollama", result);

        verify(geminiRequest).call();

        verify(ollamaChatClient).prompt();
        verify(ollamaRequest).user("Bonjour");
        verify(ollamaRequest).tools();
        verify(ollamaRequest).call();
        verify(ollamaResponse).content();
    }

    @Test
    void shouldUseGeminiForStreamResponse() {

        List<Message> messages = List.of(
                mock(Message.class)
        );

        when(geminiChatClient.prompt())
                .thenReturn(geminiRequest);

        when(geminiRequest.messages(messages))
                .thenReturn(geminiRequest);

        when(geminiRequest.tools())
                .thenReturn(geminiRequest);

        when(geminiRequest.stream())
                .thenReturn(geminiStreamResponse);

        when(geminiStreamResponse.content())
                .thenReturn(
                        Flux.just(
                                "Bonjour ",
                                "depuis ",
                                "Gemini !"
                        )
                );

        List<String> result =
                service.streamResponse(messages)
                        .collectList()
                        .block();

        assertEquals(
                List.of(
                        "Bonjour ",
                        "depuis ",
                        "Gemini !"
                ),
                result
        );

        verify(geminiChatClient).prompt();
        verify(geminiRequest).messages(messages);
        verify(geminiRequest).tools();
        verify(geminiRequest).stream();
        verify(geminiStreamResponse).content();

        verifyNoInteractions(ollamaChatClient);
    }

    @Test
    void shouldFallbackToOllamaWhenGeminiStreamFails() {

        List<Message> messages = List.of(
                mock(Message.class)
        );

        when(geminiChatClient.prompt())
                .thenReturn(geminiRequest);

        when(geminiRequest.messages(messages))
                .thenReturn(geminiRequest);

        when(geminiRequest.tools())
                .thenReturn(geminiRequest);

        when(geminiRequest.stream())
                .thenReturn(geminiStreamResponse);

        when(geminiStreamResponse.content())
                .thenReturn(
                        Flux.error(
                                new RuntimeException(
                                        "Gemini indisponible"
                                )
                        )
                );

        when(ollamaChatClient.prompt())
                .thenReturn(ollamaRequest);

        when(ollamaRequest.messages(messages))
                .thenReturn(ollamaRequest);

        when(ollamaRequest.tools())
                .thenReturn(ollamaRequest);

        when(ollamaRequest.stream())
                .thenReturn(ollamaStreamResponse);

        when(ollamaStreamResponse.content())
                .thenReturn(
                        Flux.just(
                                "Réponse ",
                                "Ollama"
                        )
                );

        List<String> result =
                service.streamResponse(messages)
                        .collectList()
                        .block();

        assertEquals(
                List.of(
                        "Réponse ",
                        "Ollama"
                ),
                result
        );

        verify(geminiRequest).stream();
        verify(geminiStreamResponse).content();

        verify(ollamaChatClient).prompt();
        verify(ollamaRequest).messages(messages);
        verify(ollamaRequest).tools();
        verify(ollamaRequest).stream();
        verify(ollamaStreamResponse).content();
    }
}