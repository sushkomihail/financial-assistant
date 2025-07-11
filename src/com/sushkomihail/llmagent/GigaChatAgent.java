package com.sushkomihail.llmagent;

import chat.giga.client.GigaChatClient;
import chat.giga.client.auth.AuthClient;
import chat.giga.client.auth.AuthClientBuilder;
import chat.giga.http.client.HttpClientException;
import chat.giga.model.Scope;
import chat.giga.model.completion.*;
import chat.giga.model.file.FileResponse;
import chat.giga.model.file.UploadFileRequest;
import com.sushkomihail.llmagent.requests.LlmAgentRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GigaChatAgent {
    private GigaChatClient client;
    private final List<ChatMessage> messagesHistory = new ArrayList<>();

    public GigaChatAgent(String authKey) {
        initClient(authKey);
    }

    public void addMessageToHistory(ChatMessageRole role, String message, List<String> attachments) {
        var messageBuilder = ChatMessage.builder();
        messageBuilder.role(role);
        messageBuilder.content(message);

        if (attachments != null && !attachments.isEmpty()) {
            for (String attachment : attachments) {
                messageBuilder.attachment(attachment);
            }
        }

        messagesHistory.add(messageBuilder.build());
    }

    public void clearMessagesHistory() {
        messagesHistory.clear();
    }

    public UUID uploadFile(String fileName, String mimeType) {
        try {
            UploadFileRequest request = UploadFileRequest.builder()
                    .file(Files.readAllBytes(Paths.get(fileName)))
                    .mimeType(mimeType)
                    .fileName(fileName)
                    .purpose("general")
                    .build();

            FileResponse response = client.uploadFile(request);
            UUID fileId = response.id();
            System.out.println("File uploaded. File id: " + response.id());
            return fileId;
        } catch (HttpClientException e) {
            System.err.println("File hasn't been uploaded. Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("File path doesn't exist. Error: " + e.getMessage());
        }

        return null;
    }

    public void deleteFile(UUID fileId) {
        client.deleteFile(fileId.toString());
    }

    public void deleteAllFiles() {
        var responsesList = client.availableFileList().data();

        for (FileResponse response : responsesList) {
            deleteFile(response.id());
        }
    }

    public CompletionResponse handleRequestWithFunction(String model, ChatFunction function) {
        try {
            var requestBuilder = CompletionRequest.builder();
            requestBuilder.model(model);
            requestBuilder.messages(messagesHistory);
            requestBuilder.temperature(0.001F);

            if (function == null) {
                return null;
            }

            requestBuilder.functionCall(ChatFunctionCall.builder()
                            .name(function.name())
                    .build());
            requestBuilder.function(function);
            return client.completions(requestBuilder.build());
        } catch (HttpClientException e) {
            System.out.println(e.statusCode() + " " + e.bodyAsString());
        }

        return null;
    }

    public CompletionResponse handleRequest(String model) {
        var requestBuilder = CompletionRequest.builder()
                .model(model)
                .messages(messagesHistory)
                .temperature(0.001F)
                .build();

        return client.completions(requestBuilder);
    }

    private void initClient(String authKey) {
        client = GigaChatClient.builder()
                .authClient(AuthClient.builder()
                        .withOAuth(AuthClientBuilder.OAuthBuilder.builder()
                                .scope(Scope.GIGACHAT_API_PERS)
                                .authKey(authKey)
                                .build())
                        .build())
                .readTimeout(180)
                .build();
    }
}
