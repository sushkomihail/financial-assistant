package com.sushkomihail.llmagent.requests;

public final class TransactionsCollectionRequest extends LlmAgentWithFileRequest {
    private static final String PROMPT = "Найди в приложенном файле все доходы и расходы. " +
            "Для этого обрати внимание на строки: 'доходы', 'расходы'. " +
            "В ответе верни ТОЛЬКО строку json в ЧЕТКОМ формате: " +
            "{\"incomes\":[{\"amount\":<сумма дохода (целое число)>, \"date\":<дата в формате yy-mm-dd>, " +
            "\"comment\":<комментарий>, \"category\":<категория дохода>}], " +
            "\"expenses\":[{\"amount\":<сумма расхода>, \"date\":<дата в формате yy-mm-dd>, " +
            "\"comment\":<комментарий>, \"category\":<категория расхода>}]} " +
            "без слова json и символов переноса строки (\\n). Не давай никаких пояснений";

    public TransactionsCollectionRequest(MimeType mimeType, String filePath) {
        super(mimeType, PROMPT, filePath);
    }
}
