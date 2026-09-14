package com.smarttrip.api.controller;

import com.smarttrip.api.dto.ConversationMessageDto;
import com.smarttrip.api.dto.ConversationSummaryDto;
import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Conversations",
        description = "Gestion des conversations SmartTrip"
)
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ChatService chatService;

    public ConversationController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Lister les conversations",
            description = "Retourne les conversations SmartTrip triées par date de dernière modification."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des conversations récupérée avec succès"
            )
    })
    @GetMapping
    public List<ConversationSummaryDto> getConversations() {

        return chatService
                .getAllConversations()
                .stream()
                .map(conversation ->
                        new ConversationSummaryDto(
                                conversation.getId(),
                                conversation.getCreatedAt(),
                                conversation.getUpdatedAt()
                        )
                )
                .toList();
    }

    @Operation(
            summary = "Récupérer les messages d'une conversation",
            description = "Retourne les messages d'une conversation dans l'ordre chronologique."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Messages récupérés avec succès"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Conversation introuvable"
            )
    })
    @GetMapping("/{id}/messages")
    public List<ConversationMessageDto> getMessages(
            @Parameter(
                    description = "Identifiant de la conversation",
                    example = "42"
            )
            @PathVariable Long id
    ) {

        return chatService
                .getMessages(id)
                .stream()
                .map(message ->
                        new ConversationMessageDto(
                                message.getId(),
                                message.getRole(),
                                message.getContent(),
                                message.getCreatedAt()
                        )
                )
                .toList();
    }
}