package com.sushkomihail.llmagent.requests;

public class LoanRecommendationRequest extends LlmAgentRequest {
    public LoanRecommendationRequest(int revenues, int expenses) {
        super("Какие рекоммендации ты можешь дать по кредитным условиям, если мои месячные доходы составили " +
                revenues + " руб., а расходы - " + expenses + " руб.?");
    }
}
