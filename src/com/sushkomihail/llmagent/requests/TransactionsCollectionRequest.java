package com.sushkomihail.llmagent.requests;

public final class TransactionsCollectionRequest extends LlmAgentWithFileRequest {
    private static final String PROMPT = "Найди в приложенном файле все доходы и расходы. " +
            "Для этого обрати внимание на строки: 'доходы', 'расходы'. " +
            "В ответе верни ТОЛЬКО строку json в ЧЕТКОМ формате: " +
            "{\"incomes\":[массив доходов (массив целых чисел)], " +
            "\"expenses\":[массив расходов (массив целых чисел)]} " +
            "без слова json и символов переноса строки (\\n). Не давай никаких пояснений";

    public TransactionsCollectionRequest(String filePath) {
        super(PROMPT, filePath);
    }
}
