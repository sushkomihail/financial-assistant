package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.ChatFunction;

public abstract class LlmAgentRequest {
    private String request;

    public LlmAgentRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public ChatFunction getLlmAgentFunction() {
        return null;
    }
}
