package com.smarttrip.api.ai;

import com.smarttrip.api.dto.PlaceDto;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.ToolCallingAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class PlaceToolObserver extends ToolCallingAdvisor {

    private static final String PLACE_TOOL_CONTEXT = "placeToolContext";

    private final ObjectMapper objectMapper;

    public PlaceToolObserver(ObjectMapper objectMapper) {
        super(
                org.springframework.ai.model.tool.ToolCallingManager.builder().build(),
                response -> response != null && response.hasToolCalls(),
                Ordered.HIGHEST_PRECEDENCE + 300,
                true
        );

        this.objectMapper = objectMapper;
    }

    @Override
    protected List<Message> doGetNextInstructionsForToolCallStream(
            ChatClientRequest chatClientRequest,
            ChatClientResponse chatClientResponse,
            ToolExecutionResult toolExecutionResult
    ) {

        collectPlaces(
                chatClientRequest,
                toolExecutionResult
        );

        return super.doGetNextInstructionsForToolCallStream(
                chatClientRequest,
                chatClientResponse,
                toolExecutionResult
        );
    }

    @Override
    protected List<Message> doGetNextInstructionsForToolCall(
            ChatClientRequest chatClientRequest,
            ChatClientResponse chatClientResponse,
            ToolExecutionResult toolExecutionResult
    ) {

        collectPlaces(
                chatClientRequest,
                toolExecutionResult
        );

        return super.doGetNextInstructionsForToolCall(
                chatClientRequest,
                chatClientResponse,
                toolExecutionResult
        );
    }

    private void collectPlaces(
            ChatClientRequest request,
            ToolExecutionResult toolExecutionResult
    ) {

        Object contextValue =
                request.context().get(PLACE_TOOL_CONTEXT);

        if (!(contextValue instanceof PlaceToolContext placeToolContext)) {
            return;
        }

        for (Message message :
                toolExecutionResult.conversationHistory()) {

            if (!(message instanceof ToolResponseMessage toolResponseMessage)) {
                continue;
            }

            toolResponseMessage
                    .getResponses()
                    .forEach(response -> {

                        try {

                            List<PlaceDto> places =
                                    objectMapper.readValue(
                                            response.responseData(),
                                            new TypeReference<List<PlaceDto>>() {}
                                    );

                            placeToolContext.addPlaces(places);

                        }
                        catch (Exception exception) {

                            System.out.println(
                                    "PLACE TOOL OBSERVER - impossible de convertir le résultat : "
                                            + exception.getMessage()
                            );
                        }
                    });
        }
    }
}