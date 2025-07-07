package com.sushkomihail;

import com.sushkomihail.llmagent.GigaChatAgent;
import com.sushkomihail.llmagent.LlmAgentController;
import com.sushkomihail.llmagent.datastructures.Expense;
import com.sushkomihail.llmagent.datastructures.ExpenseType;
import com.sushkomihail.llmagent.datastructures.Income;
import com.sushkomihail.llmagent.datastructures.NetIncomesCollection;
import com.sushkomihail.llmagent.requests.SavingsForecastRequest;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            FileInputStream configFile = new FileInputStream("gigachatapi.properties");
            props.load(configFile);
            String authKey = props.getProperty("auth_key");
            // --------------- Giga Chat -----------------
            GigaChatAgent gigaChatAgent = new GigaChatAgent(authKey);
            LlmAgentController controller = new LlmAgentController(gigaChatAgent);

            // Пример получения прогноза накоплений
            List<Integer> savings = controller.getSavingsForecast(new SavingsForecastRequest(5,
                    new NetIncomesCollection(
                            Arrays.asList(43000, 50000, 41000, 37000),
                            Arrays.asList(10000, 13000, 25000, 12500))));
            System.out.println(savings.toString());
            // controller.getLoanOffers(new LoanOfferRequest(MimeType.PDF, "sber.pdf"));
//            System.out.println(gigaChatAgent.handleRequest(
//                    new LoanOffersRequest("")));


            // --------------- Analytics -----------------
//            LoanAnalytics analytics =
//                    new LoanAnalytics(new DifferentiatedPaymentLoan(1000000, 60, 12), 100000);
//            System.out.println(analytics.generate());
//            analytics =
//                    new LoanAnalytics(new AnnuityPaymentLoan(1000000, 60, 12), 10000);
//            System.out.println();
//            System.out.println();
//            System.out.println(analytics.generate());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}