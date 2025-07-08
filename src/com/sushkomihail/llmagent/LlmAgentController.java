package com.sushkomihail.llmagent;

import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessageRole;
import chat.giga.model.completion.CompletionResponse;
import com.sushkomihail.llmagent.datastructures.LoanOffer;
import com.sushkomihail.llmagent.requests.LoanOfferRequest;
import com.sushkomihail.llmagent.requests.SavingsForecastRequest;
import com.sushkomihail.llmagent.responsehandlers.SavingsForecastResponseHandler;

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
    public List<LoanOffer> getLoanOffers(LoanOfferRequest request) {
        agent.deleteAllFiles();
        agent.clearMessagesHistory();

        UUID fileId = agent.uploadFile(request.getLoanOfferPath(), request.getMimeType().getTitle());
        agent.addMessageToHistory(ChatMessageRole.SYSTEM, request.getRequest(),
                new ArrayList<>(Collections.singletonList(fileId.toString())));

        String responseContent =
                agent.handleRequest(ModelName.GIGA_CHAT_MAX_2).choices().get(0).message().content();

        agent.addMessageToHistory(ChatMessageRole.ASSISTANT, responseContent, null);
        agent.addMessageToHistory(
                ChatMessageRole.USER, LoanOfferRequest.PARAMETER_GENERATION_REQUEST, null);

        var loanParameters = agent.handleRequestWithFunction(ModelName.GIGA_CHAT_MAX_2, request);
        // System.out.println(loanParameters);
        return null;
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

        CompletionResponse response = agent.handleRequestWithFunction(ModelName.GIGA_CHAT, request);
        SavingsForecastResponseHandler responseHandler = new SavingsForecastResponseHandler(response);
        return responseHandler.getSavings();
    }
}
