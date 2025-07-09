package com.sushkomihail.llmagent.responsehandlers;

import chat.giga.model.completion.CompletionResponse;

public abstract class LlmAgentResponseHandler<T> {
    protected final CompletionResponse response;

    public LlmAgentResponseHandler(CompletionResponse response) {
        this.response = response;
    }

    public abstract T handle();
}
