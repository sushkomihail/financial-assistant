package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.ChatFunction;

/**
 * Абстрактный класс запроса к llm-агенту
 */
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

    /**
     * Виртуальная функция для получения пользовательской функции для llm-агента
     * @return пользовательскую функцию
     */
    public ChatFunction getLlmAgentFunction() {
        return null;
    }
}
