package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.ChatFunction;

public abstract class LlmAgentRequest {
    public final String request;

    public LlmAgentRequest(String request) {
        this.request = request;
    }

    public ChatFunction getLlmAgentFunction() {
        return null;
    }
}
