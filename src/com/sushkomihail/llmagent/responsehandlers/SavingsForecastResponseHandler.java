package com.sushkomihail.llmagent.responsehandlers;

import chat.giga.model.completion.CompletionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public final class SavingsForecastResponseHandler extends LlmAgentResponseHandler<List<Integer>> {
    public SavingsForecastResponseHandler(CompletionResponse response) {
        super(response);
        handle();
    }

    @Override
    public List<Integer> handle() {
        List<Integer> savings = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String arguments =
                    mapper.writeValueAsString(response.choices().get(0).message().functionCall().arguments());
            JsonNode root = mapper.readTree(arguments);
            JsonNode array = root.get("savings");

            for (JsonNode element : array) {
                int saving = element.asInt();
                savings.add(saving);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Unable to convert to json. Error: " + e.getMessage());
        }

        return savings;
    }
}
