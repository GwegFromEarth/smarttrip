package com.smarttrip.api.model;

public record ChatRequest(
        Long conversationId,
        String message) {
}
