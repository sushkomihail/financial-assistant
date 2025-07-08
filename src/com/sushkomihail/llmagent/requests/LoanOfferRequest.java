package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.*;

public class LoanOfferRequest extends LlmAgentRequest {
    public static final String PARAMETER_GENERATION_REQUEST = "Найди название кредитного продукта, " +
            "сумму кредита, срок кредита, процентную ставку по кредиту, полную стоимость кредита";
    private static final String MAIN_REQUEST = "Ты восококвалифицированный банковский консультант. " +
            "Твоя задача найти в приложенном файле все кредитные предложения от банка. Для каждого предложения " +
            "выдели такие параметры как название кредитного продукта, сумма кредита, " +
            "срок кредита, процентная ставка по кредиту, полная стоимость кредита. Для этого обрати внимание " +
            "на строки: 'кредитный продукт/цель кредита', 'сумма кредита', 'срок возврата', " +
            "'процентная ставка', 'полная стоимость кредита/ПСК'";

    private final MimeType mimeType;
    private String loanOfferPath = "res/loanoffers/";

    public LoanOfferRequest(MimeType mimeType, String fileName) {
        super(MAIN_REQUEST);
        this.mimeType = mimeType;
        loanOfferPath += fileName;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public String getLoanOfferPath() {
        return loanOfferPath;
    }

    @Override
    public ChatFunction getLlmAgentFunction() {
        return ChatFunction.builder()
                .name("get_loan_offers")
                .description("Получение предложений по кредиту")
                .parameters(ChatFunctionParameters.builder()
                        .type("object")
                        .property("loan_offers", ChatFunctionParametersProperty.builder()
                                .type("array")
                                .description("Кредитные предложения от банка")
                                .item("type", "object")
                                .item("description", "Кредитное предложение")
                                .item("properties", ChatFunctionParametersProperty.builder()
                                        .property("product_name", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Название крединого продукта")
                                                .build())
                                        .property("amount", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Сумма кредита")
                                                .build())
                                        .property("loan_interest", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Ставка по кредиту")
                                                .build())
                                        .property("loan_term", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Срок кредита")
                                                .build())
                                        .property("full_loan_cost", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Полная стоимость кредита")
                                                .build())
                                        .build().properties())
                                .build())
                        .build())
                .fewShotExample(ChatFunctionFewShotExample.builder()
                        .request("Выдели параметры кредита. Кредитный продукт - потребительский кредит без обеспечения, " +
                                "сумма кредита - от 3 000 рублей до 30 000 000 рублей, ставка по кредиту - 21.9% - 44.5%, " +
                                "срок возврата - от 12 месяцев до 96 месяцев, диапазон ПСК - 21.9% - 44.8%")
                        .param("product_name", "потребительский кредит без обеспечения")
                        .param("amount", "от 3 000 рублей до 30 000 000 рублей")
                        .param("loan_interest", "21.9% - 44.5%")
                        .param("loan_term", "от 12 месяцев до 96 месяцев")
                        .param("full_loan_cost", "21.9% - 44.8%")
                        .build())
                .build();
    }
}
