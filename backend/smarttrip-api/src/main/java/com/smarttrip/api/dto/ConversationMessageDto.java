package com.smarttrip.api.dto;

import java.time.LocalDateTime;

public record ConversationMessageDto(
        Long id,
        String role,
        String content,
        LocalDateTime createdAt
) {
}