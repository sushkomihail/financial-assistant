package com.sushkomihail.llmagent;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.ModelName;
import chat.giga.model.Scope;
import chat.giga.model.completion.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushkomihail.llmagent.requests.LlmAgentRequest;

import java.util.ArrayList;

public class GigaChatAgent {
    private GigaChatClient client;

    public GigaChatAgent(String authKey) {
        initClient(authKey);
    }

    public String handleRequestWithFunction(LlmAgentRequest request) {
        try {
            var messages = new ArrayList<ChatMessage>();
            messages.add(ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content(request.request)
                    .build());

            var requestBuilder = CompletionRequest.builder();
            requestBuilder.model(ModelName.GIGA_CHAT);
            requestBuilder.messages(messages);

            var function = request.getLlmAgentFunction();

            if (function == null) {
                return "";
            }

            requestBuilder.function(function);
            var response =
                    client.completions(requestBuilder.build())
                            .choices().get(0).message()
                            .functionCall().arguments();

            if (response == null) {
                return "";
            }

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(response);
        } catch (HttpClientException e) {
            System.out.println(e.statusCode() + " " + e.bodyAsString());
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public String handleRequest(LlmAgentRequest request) {
        var completionRequest = CompletionRequest.builder()
                .model(ModelName.GIGA_CHAT)
                .message(ChatMessage.builder()
                        .role(ChatMessageRole.USER)
                        .content(request.request)
                        .build())
                .build();

        var response = client.completions(completionRequest).choices().get(0).message().content();
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    private void initClient(String authKey) {
        client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey(authKey)
                                .build())
                        .build())
                .build();
    }
}
