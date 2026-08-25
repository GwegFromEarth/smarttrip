package com.smarttrip.api.controller;

import com.smarttrip.api.dto.ChatRequest;
import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;

@Tag(name = "Chat", description = "Interaction avec l'assistant IA SmartTrip")
@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final ChatService chatService;

    public ChatController(ChatClient chatClient, ChatService chatService) {
        this.chatClient = chatClient;
        this.chatService = chatService;
    }

    @Operation(
            summary = "Envoyer un message à l'assistant",
            description = "Envoie un message à l'assistant IA SmartTrip et retourne la réponse complète."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Réponse générée avec succès"
            )
    })
    @GetMapping("/api/chat")
    public String chat(
            @Parameter(
                    description = "Message envoyé à l'assistant",
                    example = "Propose-moi trois lieux historiques à Rome"
            )
            @RequestParam String message) {

        return chatClient
                .prompt()
                .user(message)
                .call()
                .content();
    }

    @Operation(
            summary = "Envoyer un message en streaming",
            description = "Envoie un message à l'assistant IA SmartTrip et retourne progressivement la réponse sous forme de Server-Sent Events."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Flux de réponse généré avec succès",
                    content = @Content(
                            mediaType = MediaType.TEXT_EVENT_STREAM_VALUE
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide",
                    content = @Content
            )
    })
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
                        .map(chunk -> {

                            System.out.println(
                                    "CHUNK BACKEND : " + chunk
                            );

                            return ServerSentEvent.<String>builder()
                                    .data(chunk)
                                    .build();
                        });

        return Flux.concat(
                conversationEvent,
                responseStream
        );
    }
}