package com.sushkomihail.llmagent.responsehandlers;

import chat.giga.model.completion.CompletionResponse;

public abstract class LlmAgentResponseHandler {
    protected final CompletionResponse response;

    public LlmAgentResponseHandler(CompletionResponse response) {
        this.response = response;
    }

    protected abstract void handle();
}
