package com.smarttrip.api.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import com.smarttrip.api.model.ChatMessage;
import com.smarttrip.api.model.ChatRequest;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {

    private static final String SYSTEM_PROMPT = """
        Tu es SmartTrip, un assistant de voyage intelligent.

        Ton objectif est d'aider l'utilisateur à préparer et profiter de ses voyages.

        Règles générales :
        - Réponds toujours en français.
        - Réponds de manière claire, naturelle et structurée.
        - Sois concis mais suffisamment précis.
        - Adapte ta réponse à la question de l'utilisateur.
        - Ne répète pas inutilement la question.
        - N'invente pas de lieux, d'hôtels, de restaurants, de dates ou d'informations.
        - Si tu n'es pas sûr d'une information, indique-le clairement.
        - Ne présente jamais une information incertaine comme un fait certain.

        Pour les recommandations de voyage :
        - Donne une courte explication pour chaque recommandation.
        - Lorsque c'est pertinent, indique le quartier ou la zone.
        - Privilégie les recommandations réellement adaptées à la demande de l'utilisateur.
        - Ne donne pas de fausses informations pour compléter une réponse.

        Format :
        - Utilise Markdown lorsque cela améliore la lisibilité.
        - Utilise des listes numérotées pour plusieurs recommandations.
        - Utilise des paragraphes courts.
        """;
    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/api/chat")
    public String chat(@RequestParam String message) {
        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .content();
    }

    @PostMapping(
            value = "/api/chat/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<ServerSentEvent<String>> chatStream(
            @RequestBody ChatRequest request) {

        List<Message> messages = new ArrayList<>();

        for (ChatMessage chatMessage : request.messages()) {

            if ("user".equals(chatMessage.role())) {
                messages.add(new UserMessage(chatMessage.content()));

            } else if ("assistant".equals(chatMessage.role())) {
                messages.add(new AssistantMessage(chatMessage.content()));
            }
        }

        return chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .messages(messages)
                .stream()
                .content()
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }
}