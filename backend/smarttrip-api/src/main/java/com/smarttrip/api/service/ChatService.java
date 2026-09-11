package com.smarttrip.api.service;

import com.smarttrip.api.dto.ChatMessage;
import com.smarttrip.api.dto.ChatRequest;
import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.model.Message;
import com.smarttrip.api.model.Trip;
import com.smarttrip.api.repository.ConversationRepository;
import com.smarttrip.api.repository.MessageRepository;
import com.smarttrip.api.repository.TripRepository;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.messages.SystemMessage;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final PlaceTools placeTools;
    private final AiChatService aiChatService;
    private final RagContextService ragContextService;
    private final TripRepository tripRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            PlaceTools placeTools,
            AiChatService aiChatService,
            RagContextService ragContextService,
            TripRepository tripRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.placeTools = placeTools;
        this.aiChatService = aiChatService;
        this.ragContextService = ragContextService;
        this.tripRepository = tripRepository;
    }

    public PlaceTools getPlaceTools() {
        return placeTools;
    }

    public Conversation createConversation() {

        LocalDateTime now = LocalDateTime.now();

        Conversation conversation =
                new Conversation(now, now);

        return conversationRepository.save(conversation);
    }

    public Conversation getOrCreateConversation(Long conversationId) {

        if (conversationId == null) {
            return createConversation();
        }

        return conversationRepository.findById(conversationId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Conversation introuvable : " + conversationId
                        )
                );
    }

    public Message saveMessage(
            Conversation conversation,
            ChatMessage chatMessage
    ) {

        Message message = new Message(
                conversation,
                chatMessage.role(),
                chatMessage.content(),
                LocalDateTime.now()
        );

        conversation.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public Message saveAssistantMessage(
            Conversation conversation,
            String content
    ) {

        Message message = new Message(
                conversation,
                "assistant",
                content,
                LocalDateTime.now()
        );

        conversation.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getMessages(Long conversationId) {

        return messageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    public List<Message> getRecentMessages(Long conversationId) {

        List<Message> messages =
                messageRepository
                        .findTop20ByConversationIdOrderByCreatedAtDesc(
                                conversationId
                        );

        List<Message> chronologicalMessages =
                new ArrayList<>(messages);

        Collections.reverse(chronologicalMessages);

        return chronologicalMessages;
    }

    public List<org.springframework.ai.chat.messages.Message> buildChatHistory(
            Long conversationId
    ) {

        List<Message> history =
                getRecentMessages(conversationId);

        List<org.springframework.ai.chat.messages.Message> messages =
                new ArrayList<>();

        for (Message message : history) {

            if ("user".equals(message.getRole())) {

                messages.add(
                        new UserMessage(message.getContent())
                );

            } else if ("assistant".equals(message.getRole())) {

                messages.add(
                        new AssistantMessage(message.getContent())
                );
            }
        }

        return messages;
    }

    public Conversation prepareConversation(ChatRequest request) {

        Conversation conversation =
                getOrCreateConversation(request.conversationId());

        if (request.tripId() != null) {

            Trip trip = tripRepository.findById(request.tripId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Voyage introuvable : " + request.tripId()
                            )
                    );

            conversation.setTrip(trip);

            conversation =
                    conversationRepository.save(conversation);
        }

        ChatMessage userMessage =
                new ChatMessage(
                        "user",
                        request.message()
                );

        saveMessage(
                conversation,
                userMessage
        );

        return conversation;
    }

    private boolean isQuotaExceeded(Throwable exception) {

        Throwable current = exception;

        while (current != null) {

            if (current instanceof com.google.genai.errors.ClientException
                    && current.getMessage() != null
                    && current.getMessage().contains("429")) {

                return true;
            }

            current = current.getCause();
        }

        return false;
    }

    public Flux<String> generateResponse(
            Conversation conversation
    ) {

        // =========================================================
        // 1. HISTORIQUE
        // =========================================================

        List<org.springframework.ai.chat.messages.Message> messages =
                buildChatHistory(conversation.getId());

        Trip trip = conversation.getTrip();

        String destination =
                trip != null
                        ? trip.getDestination()
                        : null;

        List<org.springframework.ai.chat.messages.Message> messagesWithTripContext =
                buildMessagesWithTripContext(
                        messages,
                        trip,
                        destination
                );

        // =========================================================
        // 2. DERNIER MESSAGE UTILISATEUR
        // =========================================================

        String userQuestion =
                messages.stream()
                        .filter(message -> message instanceof UserMessage)
                        .map(message -> message.getText())
                        .reduce((first, second) -> second)
                        .orElse("");

        // =========================================================
        // 3. RECHERCHE RAG
        // =========================================================

        String ragContext =
                ragContextService.buildContext(
                        userQuestion,
                        destination
                );

        // =========================================================
        // 4. APPEL IA
        // =========================================================

        StringBuilder assistantResponse =
                new StringBuilder();

        return aiChatService
                .streamResponse(
                        messagesWithTripContext,
                        ragContext,
                        placeTools
                )
                .doOnNext(assistantResponse::append)
                .doOnComplete(() ->
                        saveAssistantMessage(
                                conversation,
                                assistantResponse.toString()
                        )
                )
                .onErrorResume(exception -> {

                    if (isQuotaExceeded(exception)) {

                        return Flux.just(
                                "Désolé, le service IA a temporairement atteint " +
                                        "sa limite d'utilisation. " +
                                        "Veuillez réessayer un peu plus tard."
                        );
                    }

                    return Flux.just(
                            "Désolé, le service IA est temporairement indisponible. " +
                                    "Veuillez réessayer dans quelques instants."
                    );
                });
    }

    private List<org.springframework.ai.chat.messages.Message> buildMessagesWithTripContext(
            List<org.springframework.ai.chat.messages.Message> messages,
            Trip trip,
            String destination
    ) {

        if (trip == null) {
            return messages;
        }

        List<org.springframework.ai.chat.messages.Message> enrichedMessages =
                new ArrayList<>();

        enrichedMessages.add(
                new SystemMessage(
                        """
                        CONTEXTE DU VOYAGE SMARTTRIP
    
                        Destination : %s
                        Date de début : %s
                        Date de fin : %s
                        Nombre de voyageurs : %d
                        Préférences : %s
    
                        Utilise ces informations pour personnaliser tes réponses
                        lorsque cela est pertinent.
                        """.formatted(
                                destination,
                                trip.getStartDate(),
                                trip.getEndDate(),
                                trip.getTravelers(),
                                trip.getPreferences()
                        )
                )
        );

        enrichedMessages.addAll(messages);

        return enrichedMessages;
    }
}