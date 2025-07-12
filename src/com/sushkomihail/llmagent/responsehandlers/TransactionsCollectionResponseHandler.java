package com.sushkomihail.llmagent.responsehandlers;

import chat.giga.model.completion.CompletionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.IncomeDTO;
import com.kolesnikovroman.LoanOfferDTO;
import com.sushkomihail.datastructures.TransactionsCollection;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionsCollectionResponseHandler extends LlmAgentResponseHandler<TransactionsCollection> {
    public TransactionsCollectionResponseHandler(CompletionResponse response) {
        super(response);
    }

    @Override
    public TransactionsCollection handle() {
        TransactionsCollection collection = new TransactionsCollection();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String arguments = response.choices().get(0).message().content();
            JsonNode root = mapper.readTree(arguments);
            JsonNode incomesArray = root.get("incomes");
            JsonNode expensesArray = root.get("expenses");

            for (JsonNode element : incomesArray) {
                BigDecimal amount = BigDecimal.valueOf(element.get("amount").asInt());
                String[] dateProps = element.get("date").asText().split("-");
                LocalDate date = LocalDate.of(
                        Integer.parseInt(dateProps[0]),
                        Integer.parseInt(dateProps[1]),
                        Integer.parseInt(dateProps[2])
                );
                String comment = element.get("comment").asText();
                String category = element.get("category").asText();
                collection.addIncome(new IncomeDTO(0, amount, date, comment, category));
            }

            for (JsonNode element : expensesArray) {
                BigDecimal amount = BigDecimal.valueOf(element.get("amount").asInt());
                String[] dateProps = element.get("date").asText().split("-");
                LocalDate date = LocalDate.of(
                        Integer.parseInt(dateProps[0]),
                        Integer.parseInt(dateProps[1]),
                        Integer.parseInt(dateProps[2])
                );
                String comment = element.get("comment").asText();
                String category = element.get("category").asText();
                collection.addExpense(new ExpenseDTO(0, amount, date, comment, category));
            }
        } catch (JsonProcessingException e) {
            System.err.println("[ERROR][class LoanOffersRequestHandler]: Unable to convert to json. " +
                    "Error: " + e.getMessage());
        }

        return collection;
    }

    public long defineMinId() {
        return 0;
    }
}
