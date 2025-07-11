package com.sushkomihail.llmagent.requests;

import chat.giga.model.completion.*;
import com.kolesnikovroman.LoanOfferDTO;

public class LoanOfferRequest extends LlmAgentRequest {
    public static final String PARAMETER_GENERATION_REQUEST = "Передай данные из ответа в качестве параметров в " +
            "функцию";
    private static final String MAIN_REQUEST = "Ты высококвалифицированный банковский консультант. " +
            "Твоя задача найти в приложенном файле все кредитные предложения от банка. Для каждого предложения " +
            "выдели основные параметры. Для этого обрати внимание на строки: 'кредитный продукт/цель кредита', " +
            "'сумма кредита', 'срок возврата', 'процентная ставка', 'полная стоимость кредита/ПСК'. " +
            "В ответе верни ТОЛЬКО найденные параметры в ЧЕТКОМ виде 'параметр: значение'. " +
            "Не давай никаких пояснений";

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
                .description("Получает предложения по кредиту от банка")
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
                                                .description("Название кредитного продукта")
                                                .build())
                                        .property("amount", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Сумма кредита")
                                                .build())
                                        .property("rate", ChatFunctionParametersProperty.builder()
                                                .type("string")
                                                .description("Ставка по кредиту")
                                                .build())
                                        .property("term", ChatFunctionParametersProperty.builder()
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
                        .request("кредитный продукт: Потребительский кредит без обеспечения: " +
                                "сумма кредита: от 3 000 рублей до 30 000 000 рублей; " +
                                "срок кредита: от 1 месяца до 60 месяцев;" +
                                "процентная ставка: 21,9% - 44,5%; " +
                                "полная стоимость кредита (ПСК): 21,900% - 44,800%. " +
                                "Передай данные из ответа в качестве параметров в функцию")
                        .param("loan_offers", new LoanOfferDTO[] { new LoanOfferDTO(
                                null,
                                "Потребительский кредит без обеспечения",
                                "от 3 000 рублей до 30 000 000 рублей",
                                "21.9% - 44.5%",
                                "от 12 месяцев до 96 месяцев",
                                "21.9% - 44.8%") })
                        .build())
                .build();
    }
}
