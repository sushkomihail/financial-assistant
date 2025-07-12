package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.ChatFunction;

public abstract class LlmAgentRequest {
    private final String request;

    public LlmAgentRequest(String request) {
        this.request = request;
    }

    public String getRequest() {
        return request;
    }

    /**
     * Виртуальная функция для получения пользовательской функции для llm-агента
     * @return пользовательскую функцию
     */
    public ChatFunction getLlmAgentFunction() {
        return null;
    }
}
