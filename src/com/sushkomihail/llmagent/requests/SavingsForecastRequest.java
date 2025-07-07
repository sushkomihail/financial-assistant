package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.ChatFunction;
import chat.giga.model.completion.ChatFunctionParameters;
import chat.giga.model.completion.ChatFunctionParametersProperty;
import com.sushkomihail.llmagent.datastructures.NetIncomesCollection;

public class SavingsForecastRequest extends LlmAgentRequest {
    public static final String PARAMETER_GENERATION_REQUEST = "Передай посчитанные значения в функцию";

    public SavingsForecastRequest(int forecastPeriod, NetIncomesCollection netIncomes) {
        super("Ты высококвалифицированный статистик. Твоя задача посчитать ожидаемые накопления " +
                "на ближайшие " + forecastPeriod + " месяц(-а/-ев) вперед. На вход тебе дан массив из " +
                "чистых доходов за прошлые месяцы: " + netIncomes.toString() + ". В ответе верни " +
                "ТОЛЬКО посчитанные значения ожидаемого дохода на каждый из " + forecastPeriod + " месяца(-ев). " +
                "Не давай никаких пояснений");
    }

    @Override
    public ChatFunction getLlmAgentFunction() {
        return ChatFunction.builder()
                .name("get_savings_forecast")
                .description("Получение прогноза по накоплениям")
                .parameters(ChatFunctionParameters.builder()
                        .type("object")
                        .property("savings", ChatFunctionParametersProperty.builder()
                                .type("array")
                                .description("Ожидаемые накопления")
                                .item("type", "integer")
                                .build())
                        .build())
                .build();
    }
}
