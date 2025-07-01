package com.sushkomihail.llmagent.requests;

public class SavingsForecastRequest extends LlmAgentRequest {

    public SavingsForecastRequest(int forecastPeriod, int revenuesAmount, String expenses) {
        super("Покажи прогноз по накоплениям на ближайшие " + forecastPeriod +
                ", исходя из общих доходов - " + revenuesAmount +
                " и расходов " + expenses + " за месяц");
    }
}
