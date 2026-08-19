package com.smarttrip.api.dto;

public record ChatRequest(
        Long conversationId,
        String message) {
}
