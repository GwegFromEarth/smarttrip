package com.smarttrip.api.service;

import com.smarttrip.api.dto.ChatMessage;
import com.smarttrip.api.dto.ChatRequest;
import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.model.Message;
import com.smarttrip.api.repository.ConversationRepository;
import com.smarttrip.api.repository.MessageRepository;
import org.springframework.stereotype.Service;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;

import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ChatClient chatClient;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ChatClient chatClient
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.chatClient = chatClient;
    }

    public Conversation createConversation() {

        LocalDateTime now = LocalDateTime.now();

        Conversation conversation = new Conversation(now, now);

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

    public List<org.springframework.ai.chat.messages.Message> buildChatHistory(
            Long conversationId
    ) {

        List<Message> history = getMessages(conversationId);

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

        ChatMessage userMessage = new ChatMessage(
                "user",
                request.message()
        );

        saveMessage(conversation, userMessage);

        return conversation;
    }

    public Flux<String> generateResponse(
            Conversation conversation
    ) {

        List<org.springframework.ai.chat.messages.Message> messages =
                buildChatHistory(conversation.getId());

        StringBuilder assistantResponse = new StringBuilder();

        return chatClient
                .prompt()
                .messages(messages)
                .stream()
                .content()
                .doOnNext(assistantResponse::append)
                .doOnComplete(() ->
                        saveAssistantMessage(
                                conversation,
                                assistantResponse.toString()
                        )
                );
    }
}
