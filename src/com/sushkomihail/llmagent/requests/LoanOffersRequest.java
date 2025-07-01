package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.*;

public class LoanOffersRequest extends LlmAgentRequest {
    public LoanOffersRequest(int loanAmount, int loanTerm) {
        super("Покажи актуальные предложения по потребительскому кредиту суммой " +
                loanAmount + " руб. на срок " + loanTerm + " месяцев от разных банков");
    }

    @Override
    public ChatFunction getLlmAgentFunction() {
        return ChatFunction.builder()
                .name("get_loan_offers_selection")
                .description("Получение наиболее выгодных предложений по кредиту")
                .parameters(ChatFunctionParameters.builder()
                        .type("object")
                        .property("loan_offers", ChatFunctionParametersProperty.builder()
                                .type("array")
                                .description("Выборка кредитных предложений от банков")
                                .item("type", "object")
                                .item("description", "Кредитное предложение от банка")
                                .item("properties", ChatFunctionParametersProperty.builder()
                                        .property("bank_name", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Название банка")
                                                .build())
                                        .property("loan_interest", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Ставка по кредиту")
                                                .build())
                                        .build().properties())
                                .build())
                        .build())
                .build();
    }
}
