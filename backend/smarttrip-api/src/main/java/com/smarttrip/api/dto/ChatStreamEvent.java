package com.smarttrip.api.dto;

public record ChatStreamEvent(
        String type,
        Object data
) {
}