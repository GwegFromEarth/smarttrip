package com.smarttrip.api.controller;

import com.smarttrip.api.dto.ChatRequest;
import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final ChatService chatService;

    public ChatController(ChatClient chatClient, ChatService chatService) {
        this.chatClient = chatClient;
        this.chatService = chatService;
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @PostMapping(
            value = "/api/chat/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ServerSentEvent<String>> chatStream(
            @RequestBody ChatRequest request
    ) {

        Conversation conversation =
                chatService.prepareConversation(request);

        Flux<ServerSentEvent<String>> conversationEvent =
                Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("conversation")
                                .data(String.valueOf(conversation.getId()))
                                .build()
                );

        Flux<ServerSentEvent<String>> responseStream =
                chatService
                        .generateResponse(conversation)
                        .map(chunk ->
                                ServerSentEvent.<String>builder()
                                        .data(chunk)
                                        .build()
                        );

        return Flux.concat(
                conversationEvent,
                responseStream
        );
    }
}