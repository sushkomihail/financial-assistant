package com.sushkomihail.llmagent;

import chat.giga.model.ModelName;
import chat.giga.model.completion.ChatMessageRole;
import com.sushkomihail.llmagent.datastructures.LoanOffer;
import com.sushkomihail.llmagent.requests.LoanOfferRequest;

import java.util.*;

public class LlmAgentController {
    private final GigaChatAgent agent;

    public LlmAgentController(GigaChatAgent agent) {
        this.agent = agent;
    }

    public List<LoanOffer> getLoanOffers(LoanOfferRequest request) {
        agent.deleteAllFiles();
        agent.clearMessagesHistory();

        UUID fileId = agent.uploadFile(request.getLoanOfferPath(), request.getMimeType().getTitle());
        agent.addMessageToHistory(ChatMessageRole.SYSTEM, request.getRequest(),
                new ArrayList<>(Collections.singletonList(fileId.toString())));

        String response = agent.handleRequest(ModelName.GIGA_CHAT_MAX_2);
        System.out.println(response);

        agent.addMessageToHistory(ChatMessageRole.ASSISTANT, response, null);
        agent.addMessageToHistory(ChatMessageRole.USER, LoanOfferRequest.PARAMETER_GENERATION_REQUEST, null);

        String loanParameters = agent.handleRequestWithFunction(ModelName.GIGA_CHAT_MAX_2, request);
        System.out.println(loanParameters);
        return null;
    }
}
