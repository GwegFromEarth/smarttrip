package com.smarttrip.api.service;

import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.model.Message;
import com.smarttrip.api.repository.ConversationRepository;
import com.smarttrip.api.repository.MessageRepository;
import com.smarttrip.api.repository.TripRepository;
import com.smarttrip.api.dto.ChatRequest;
import com.smarttrip.api.model.Trip;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PlaceTools placeTools;

    @Mock
    private AiChatService aiChatService;

    @Mock
    private RagContextService ragContextService;

    @Mock
    private TripRepository tripRepository;

    private ChatService chatService;

    @BeforeEach
    void setUp() {

        chatService = new ChatService(
                conversationRepository,
                messageRepository,
                placeTools,
                aiChatService,
                ragContextService,
                tripRepository
        );
    }

    @Test
    void shouldBuildRagContextAndPassItToAiChatService() {

        // =========================================================
        // GIVEN
        // =========================================================

        Long conversationId = 1L;

        Conversation conversation =
                mock(Conversation.class);

        when(conversation.getId())
                .thenReturn(conversationId);

        Message userMessage =
                new Message(
                        conversation,
                        "user",
                        "Quels sont les principaux lieux historiques à Rome ?",
                        LocalDateTime.now()
                );

        String ragContext = """
                Rome est une destination majeure pour découvrir
                l'histoire antique.

                Le Colisée, le Forum romain et le Panthéon
                sont des lieux historiques majeurs.
                """;

        when(messageRepository
                .findTop20ByConversationIdOrderByCreatedAtDesc(
                        conversationId
                ))
                .thenReturn(List.of(userMessage));

        when(ragContextService.buildContext(
                "Quels sont les principaux lieux historiques à Rome ?",
                null
        ))
                .thenReturn(ragContext);

        when(aiChatService.streamResponse(
                anyList(),
                eq(ragContext),
                eq(placeTools)
        ))
                .thenReturn(
                        Flux.just(
                                "Voici les principaux lieux historiques de Rome."
                        )
                );

        // =========================================================
        // WHEN
        // =========================================================

        Flux<String> response =
                chatService.generateResponse(
                        conversation
                );

        // =========================================================
        // THEN
        // =========================================================

        StepVerifier.create(response)
                .expectNext(
                        "Voici les principaux lieux historiques de Rome."
                )
                .verifyComplete();

        verify(ragContextService)
                .buildContext(
                        "Quels sont les principaux lieux historiques à Rome ?",
                        null
                );

        verify(aiChatService)
                .streamResponse(
                        anyList(),
                        eq(ragContext),
                        eq(placeTools)
                );
    }

    @Test
    void shouldContinueWithoutRagContextWhenContextIsEmpty() {

        // =========================================================
        // GIVEN
        // =========================================================

        Long conversationId = 2L;

        Conversation conversation =
                mock(Conversation.class);

        when(conversation.getId())
                .thenReturn(conversationId);

        Message userMessage =
                new Message(
                        conversation,
                        "user",
                        "Bonjour",
                        LocalDateTime.now()
                );

        when(messageRepository
                .findTop20ByConversationIdOrderByCreatedAtDesc(
                        conversationId
                ))
                .thenReturn(List.of(userMessage));

        when(ragContextService.buildContext(
                "Bonjour",
                null
        ))
                .thenReturn("");

        when(aiChatService.streamResponse(
                anyList(),
                eq(""),
                eq(placeTools)
        ))
                .thenReturn(
                        Flux.just(
                                "Bonjour ! Comment puis-je vous aider ?"
                        )
                );

        // =========================================================
        // WHEN
        // =========================================================

        Flux<String> response =
                chatService.generateResponse(
                        conversation
                );

        // =========================================================
        // THEN
        // =========================================================

        StepVerifier.create(response)
                .expectNext(
                        "Bonjour ! Comment puis-je vous aider ?"
                )
                .verifyComplete();

        verify(ragContextService)
                .buildContext(
                        "Bonjour",
                        null
                );

        verify(aiChatService)
                .streamResponse(
                        anyList(),
                        eq(""),
                        eq(placeTools)
                );
    }

    @Test
    void shouldUseLastUserMessageForRagSearch() {

        // =========================================================
        // GIVEN
        // =========================================================

        Long conversationId = 3L;

        Conversation conversation =
                mock(Conversation.class);

        when(conversation.getId())
                .thenReturn(conversationId);

        Message firstUserMessage =
                new Message(
                        conversation,
                        "user",
                        "Je veux visiter Rome.",
                        LocalDateTime.now().minusMinutes(2)
                );

        Message assistantMessage =
                new Message(
                        conversation,
                        "assistant",
                        "Très bien ! Que souhaitez-vous découvrir ?",
                        LocalDateTime.now().minusMinutes(1)
                );

        Message lastUserMessage =
                new Message(
                        conversation,
                        "user",
                        "Je cherche surtout des lieux historiques.",
                        LocalDateTime.now()
                );

        /*
         * Le repository retourne les messages du plus récent
         * au plus ancien.
         *
         * ChatService.getRecentMessages() inverse ensuite
         * cette liste pour reconstruire l'historique chronologique.
         */
        when(messageRepository
                .findTop20ByConversationIdOrderByCreatedAtDesc(
                        conversationId
                ))
                .thenReturn(
                        List.of(
                                lastUserMessage,
                                assistantMessage,
                                firstUserMessage
                        )
                );

        when(ragContextService.buildContext(
                "Je cherche surtout des lieux historiques.",
                null
        ))
                .thenReturn(
                        "Contexte historique de Rome."
                );

        when(aiChatService.streamResponse(
                anyList(),
                eq("Contexte historique de Rome."),
                eq(placeTools)
        ))
                .thenReturn(
                        Flux.just("Réponse")
                );

        // =========================================================
        // WHEN
        // =========================================================

        Flux<String> response =
                chatService.generateResponse(
                        conversation
                );

        // =========================================================
        // THEN
        // =========================================================

        StepVerifier.create(response)
                .expectNext("Réponse")
                .verifyComplete();

        verify(ragContextService)
                .buildContext(
                        "Je cherche surtout des lieux historiques.",
                        null
                );

        verify(aiChatService)
                .streamResponse(
                        anyList(),
                        eq("Contexte historique de Rome."),
                        eq(placeTools)
                );
    }

    @Test
    void shouldAssociateTripToConversation() {

        // =========================================================
        // GIVEN
        // =========================================================

        Long tripId = 10L;

        Conversation conversation =
                mock(Conversation.class);

        Trip trip =
                mock(Trip.class);

        ChatRequest request =
                new ChatRequest(
                        null,
                        tripId,
                        "Quels lieux historiques visiter ?"
                );

        when(conversationRepository.save(any(Conversation.class)))
                .thenReturn(conversation);

        when(tripRepository.findById(tripId))
                .thenReturn(java.util.Optional.of(trip));

        // =========================================================
        // WHEN
        // =========================================================

        chatService.prepareConversation(request);

        // =========================================================
        // THEN
        // =========================================================

        verify(tripRepository)
                .findById(tripId);

        verify(conversation)
                .setTrip(trip);
    }

    @Test
    void shouldUseTripDestinationForRagSearch() {

        // =========================================================
        // GIVEN
        // =========================================================

        Long conversationId = 4L;
        Long tripId = 10L;

        Conversation conversation =
                mock(Conversation.class);

        Trip trip =
                mock(Trip.class);

        when(conversation.getId())
                .thenReturn(conversationId);

        when(conversation.getTrip())
                .thenReturn(trip);

        when(trip.getDestination())
                .thenReturn("Rome");

        Message userMessage =
                new Message(
                        conversation,
                        "user",
                        "Quels lieux historiques visiter ?",
                        LocalDateTime.now()
                );

        when(messageRepository
                .findTop20ByConversationIdOrderByCreatedAtDesc(
                        conversationId
                ))
                .thenReturn(List.of(userMessage));

        when(ragContextService.buildContext(
                "Quels lieux historiques visiter ?",
                "Rome"
        ))
                .thenReturn(
                        "Contexte historique de Rome."
                );

        when(aiChatService.streamResponse(
                anyList(),
                eq("Contexte historique de Rome."),
                eq(placeTools)
        ))
                .thenReturn(
                        Flux.just(
                                "Voici les lieux historiques à visiter."
                        )
                );

        // =========================================================
        // WHEN
        // =========================================================

        Flux<String> response =
                chatService.generateResponse(
                        conversation
                );

        // =========================================================
        // THEN
        // =========================================================

        StepVerifier.create(response)
                .expectNext(
                        "Voici les lieux historiques à visiter."
                )
                .verifyComplete();

        /*
         * On vérifie le comportement fonctionnel :
         * la destination du voyage est bien utilisée
         * pour effectuer la recherche RAG.
         *
         * Pas de vérification du nombre d'appels à getTrip(),
         * car ChatService peut légitimement y accéder plusieurs fois.
         */
        verify(trip)
                .getDestination();

        verify(ragContextService)
                .buildContext(
                        "Quels lieux historiques visiter ?",
                        "Rome"
                );

        verify(aiChatService)
                .streamResponse(
                        anyList(),
                        eq("Contexte historique de Rome."),
                        eq(placeTools)
                );
    }
}