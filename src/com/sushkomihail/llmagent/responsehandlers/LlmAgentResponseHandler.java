package com.sushkomihail.llmagent.responsehandlers;

import chat.giga.model.completion.CompletionResponse;

/**
 * Абстрактный класс обработчика ответа llm-агента
 * @param <T>
 */
public abstract class LlmAgentResponseHandler<T> {
    protected final CompletionResponse response;

    public LlmAgentResponseHandler(CompletionResponse response) {
        this.response = response;
    }

    /**
     * Функция обработки ответа
     * @return необходимую структуру данных
     */
    public abstract T handle();
}
