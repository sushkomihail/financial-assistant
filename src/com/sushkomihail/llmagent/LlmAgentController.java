package com.sushkomihail.llmagent;

import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionResponse;
import com.kolesnikovroman.LoanOfferDTO;
import com.sushkomihail.datastructures.TransactionsCollection;
import com.sushkomihail.llmagent.requests.LlmAgentWithFileRequest;
import com.sushkomihail.llmagent.requests.LoanOffersRequest;
import com.sushkomihail.llmagent.requests.SavingsForecastRequest;
import com.sushkomihail.llmagent.requests.TransactionsCollectionRequest;
import com.sushkomihail.llmagent.responsehandlers.LoanOffersResponseHandler;
import com.sushkomihail.llmagent.responsehandlers.SavingsForecastResponseHandler;
import com.sushkomihail.llmagent.responsehandlers.TransactionsCollectionResponseHandler;

import java.util.*;

public class LlmAgentController {
    private final GigaChatAgent agent;

    public LlmAgentController(GigaChatAgent agent) {
        this.agent = agent;
    }

    /**
     * Получение массива кредитных предложений от определенного банка
     * @param request - объект содержащий запрос к нейросети
     * @return массив кредитных предложений
     */
    public List<LoanOfferDTO> getLoanOffers(String bankName, LoanOffersRequest request) {
        agent.deleteAllFiles();
        agent.clearMessagesHistory();

        UUID fileId = agent.uploadFile(request.getLoanOfferPath(), request.getMimeType().getTitle());
        agent.addMessageToHistory(ChatMessageRole.SYSTEM, request.getRequest(),
                new ArrayList<>(Collections.singletonList(fileId.toString())));

        CompletionResponse response = agent.handleRequest(ModelName.GIGA_CHAT);
        LoanOffersResponseHandler responseHandler = new LoanOffersResponseHandler(bankName, response);
        return responseHandler.handle();
    }

    /**
     * Получение массива ожидаемых доходов на n месяцев вперед
     * @param request - объект содержащий запрос к нейросети
     * @return массив целых чисел
     */
    public List<Integer> getSavingsForecast(SavingsForecastRequest request) {
        agent.deleteAllFiles();
        agent.clearMessagesHistory();

        agent.addMessageToHistory(ChatMessageRole.SYSTEM, request.getRequest(), null);
        String responseContent = agent.handleRequest(ModelName.GIGA_CHAT).choices().get(0).message().content();

        agent.addMessageToHistory(ChatMessageRole.ASSISTANT, responseContent, null);
        agent.addMessageToHistory(
                ChatMessageRole.USER, SavingsForecastRequest.PARAMETER_GENERATION_REQUEST, null);

        CompletionResponse response =
                agent.handleRequestWithFunction(ModelName.GIGA_CHAT, request.getLlmAgentFunction());
        SavingsForecastResponseHandler responseHandler = new SavingsForecastResponseHandler(response);
        return responseHandler.handle();
    }

    /**
     * Получение доходов и расходов из загруженного файла
     * @return коллекция, содержащая массивы доходов и расходов
     */
    public TransactionsCollection getTransactions(TransactionsCollectionRequest request) {
        CompletionResponse response = getWithFileRequestResponse(request);
        System.out.println(response);
        TransactionsCollectionResponseHandler responseHandler = new TransactionsCollectionResponseHandler(response);
        return responseHandler.handle();
    }

    /**
     * Получение ответа на запрос с файлом
     * @param request - объект содержащий запрос к нейросети
     * @return объект, содержащий ответ нейросети
     */
    private CompletionResponse getWithFileRequestResponse(LlmAgentWithFileRequest request) {
        agent.deleteAllFiles();
        agent.clearMessagesHistory();

        UUID fileId = agent.uploadFile(request.getFilePath(), request.getMimeType().getTitle());
        agent.addMessageToHistory(ChatMessageRole.SYSTEM, request.getRequest(),
                new ArrayList<>(Collections.singletonList(fileId.toString())));

        return agent.handleRequest(ModelName.GIGA_CHAT);
    }
}
