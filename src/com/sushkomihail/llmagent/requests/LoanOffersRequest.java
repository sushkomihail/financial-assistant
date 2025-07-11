package com.sushkomihail.llmagent.requests;

public final class LoanOffersRequest extends LlmAgentRequest {
    private static final String MAIN_REQUEST = "Ты высококвалифицированный банковский консультант. " +
            "Твоя задача найти в приложенном файле все кредитные предложения от банка. Для каждого предложения " +
            "выдели основные параметры. Для этого обрати внимание на строки: 'кредитный продукт/цель кредита', " +
            "'сумма кредита', 'срок возврата', 'процентная ставка', 'полная стоимость кредита/ПСК'. " +
            "В ответе верни ТОЛЬКО строку json в ЧЕТКОМ формате: " +
            "{\"loan_offers\":[{\"product_name\":<кредитный продукт>, \"amount\":<сумма кредита>, " +
            "\"term\":<срок>, \"rate\":<процентная ставка>, \"full_loan_cost\":<полная стоимость кредита>}]} " +
            "без слова json и символов переноса строки (\\n). Не давай никаких пояснений";

    private final MimeType mimeType;
    private String loanOfferPath = "res/loanoffers/";

    public LoanOffersRequest(MimeType mimeType, String fileName) {
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
}
