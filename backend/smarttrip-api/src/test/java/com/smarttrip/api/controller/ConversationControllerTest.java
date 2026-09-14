package com.smarttrip.api.controller;

import com.smarttrip.api.model.Conversation;
import com.smarttrip.api.model.Message;
import com.smarttrip.api.service.ChatService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConversationController.class)
class ConversationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @TestConfiguration
    static class TestCacheConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Test
    void shouldGetConversations() throws Exception {

        // =========================================================
        // GIVEN
        // =========================================================

        LocalDateTime firstCreatedAt =
                LocalDateTime.of(2026, 9, 10, 10, 0);

        LocalDateTime firstUpdatedAt =
                LocalDateTime.of(2026, 9, 10, 11, 0);

        LocalDateTime secondCreatedAt =
                LocalDateTime.of(2026, 9, 11, 10, 0);

        LocalDateTime secondUpdatedAt =
                LocalDateTime.of(2026, 9, 11, 12, 0);

        Conversation firstConversation =
                new Conversation(
                        firstCreatedAt,
                        firstUpdatedAt
                );

        Conversation secondConversation =
                new Conversation(
                        secondCreatedAt,
                        secondUpdatedAt
                );

        when(chatService.getAllConversations())
                .thenReturn(
                        List.of(
                                secondConversation,
                                firstConversation
                        )
                );

        // =========================================================
        // WHEN / THEN
        // =========================================================

        mockMvc.perform(
                        get("/api/conversations")
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "createdAt": "2026-09-11T10:00:00",
                            "updatedAt": "2026-09-11T12:00:00"
                          },
                          {
                            "createdAt": "2026-09-10T10:00:00",
                            "updatedAt": "2026-09-10T11:00:00"
                          }
                        ]
                        """));
    }

    @Test
    void shouldGetConversationMessages() throws Exception {

        // =========================================================
        // GIVEN
        // =========================================================

        Long conversationId = 42L;

        Conversation conversation =
                new Conversation(
                        LocalDateTime.of(2026, 9, 10, 10, 0),
                        LocalDateTime.of(2026, 9, 10, 11, 0)
                );

        Message userMessage =
                new Message(
                        conversation,
                        "user",
                        "Que visiter à Rome ?",
                        LocalDateTime.of(2026, 9, 10, 10, 1)
                );

        Message assistantMessage =
                new Message(
                        conversation,
                        "assistant",
                        "Le Colisée est incontournable.",
                        LocalDateTime.of(2026, 9, 10, 10, 2)
                );

        when(chatService.getMessages(conversationId))
                .thenReturn(
                        List.of(
                                userMessage,
                                assistantMessage
                        )
                );

        // =========================================================
        // WHEN / THEN
        // =========================================================

        mockMvc.perform(
                        get("/api/conversations/{id}/messages", conversationId)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "role": "user",
                            "content": "Que visiter à Rome ?"
                          },
                          {
                            "role": "assistant",
                            "content": "Le Colisée est incontournable."
                          }
                        ]
                        """));
    }
}