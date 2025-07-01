package com.sushkomihail;

import com.sushkomihail.llmagent.GigaChatAgent;
import com.sushkomihail.llmagent.requests.LlmAgentRequest;
import com.sushkomihail.llmagent.requests.LoanOffersRequest;
import com.sushkomihail.llmagent.requests.LoanRecommendationRequest;
import com.sushkomihail.llmagent.requests.SavingsForecastRequest;
import com.sushkomihail.llmagent.responses.ILlmAgentResponse;
import com.sushkomihail.llmagent.responses.LoanOffersResponse;

import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            FileInputStream configFile = new FileInputStream("gigachatapi.properties");
            props.load(configFile);
            String authKey = props.getProperty("auth_key");
            GigaChatAgent gigaChatModel = new GigaChatAgent(authKey);

            int loanAmount = 1000000;
            int loanTerm = 12;
            LlmAgentRequest request = new LoanOffersRequest(loanAmount, loanTerm);
            ILlmAgentResponse response = new LoanOffersResponse(loanAmount, loanTerm);

            System.out.println(gigaChatModel.handleRequest(
                    new SavingsForecastRequest(3, 100000,
                            "одежда - 5000, жкх - 10000, транспорт - 5000, продукты - 10000")));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}