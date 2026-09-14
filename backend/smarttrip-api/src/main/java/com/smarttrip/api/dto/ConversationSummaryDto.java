package com.smarttrip.api.dto;

import java.time.LocalDateTime;

public record ConversationSummaryDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}