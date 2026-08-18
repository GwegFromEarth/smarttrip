package com.smarttrip.api.model;

import java.util.List;

public record ChatRequest(
        List<ChatMessage> messages
) {
}
