package com.sushkomihail.llmagent.responsehandlers;

import chat.giga.model.completion.CompletionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolesnikovroman.LoanOfferDTO;
import com.sushkomihail.datastructures.TransactionsCollection;

public class TransactionsCollectionResponseHandler extends LlmAgentResponseHandler<TransactionsCollection> {
    public TransactionsCollectionResponseHandler(CompletionResponse response) {
        super(response);
    }

    @Override
    public TransactionsCollection handle() {
//        TransactionsCollection collection = new TransactionsCollection();
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            String arguments = response.choices().get(0).message().content();
//            JsonNode root = mapper.readTree(arguments);
//            JsonNode incomesArray = root.get("incomes");
//            JsonNode expensesArray = root.get("expenses");
//
//            for (JsonNode element : array) {
//                String productName = element.get("product_name").asText();
//                String amount = element.get("amount").asText();
//                String rate = element.get("rate").asText();
//                String term = element.get("term").asText();
//                String fullLoanCost = element.get("full_loan_cost").asText();
//                offers.add(new LoanOfferDTO(bankName, productName, amount, rate, term, fullLoanCost));
//            }
//        } catch (JsonProcessingException e) {
//            System.err.println("[ERROR][class LoanOffersRequestHandler]: Unable to convert to json. " +
//                    "Error: " + e.getMessage());
//        }

        return null;
    }

    public long defineMinId() {
        return 0;
    }
}
