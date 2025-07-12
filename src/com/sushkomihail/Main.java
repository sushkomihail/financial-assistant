package com.sushkomihail;

import com.kolesnikovroman.CreditOfferRepository;
import com.kolesnikovroman.LoanOfferDTO;
import com.sushkomihail.llmagent.GigaChatAgent;
import com.sushkomihail.llmagent.LlmAgentController;
import com.sushkomihail.llmagent.requests.LoanOffersRequest;
import com.sushkomihail.llmagent.requests.MimeType;

import java.io.FileInputStream;
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
//            List<Integer> savings = controller.getSavingsForecast(new SavingsForecastRequest(5,
//                    new NetIncomesCollection(
//                            Arrays.asList(43000, 50000, 41000, 37000),
//                            Arrays.asList(10000, 13000, 25000, 12500))));
//            System.out.println(savings.toString());

            // Пример получения кредитных предложений от конкретного банка
            List<LoanOfferDTO> loanOffers = controller.getLoanOffers(
                    null,
                    new LoanOffersRequest(MimeType.PDF, "C:/AltSTU/OOP/Lab2/02_21_Sushko.pdf", true));
            System.out.println(loanOffers.size());


            // --------------- Analytics -----------------
//            LoanAnalytics analytics =
//                    new LoanAnalytics(new DifferentiatedPaymentLoan(1000000, 60, 12), 100000);
//            System.out.println(analytics.generate());
//            analytics =
//                    new LoanAnalytics(new AnnuityPaymentLoan(1000000, 60, 12), 10000);
//            System.out.println(analytics.generate());

            // --------------- Database ------------------
//            CreditOfferRepository repository = new CreditOfferRepository();
//            var offers = repository.findAll();
//            System.out.println(offers);
//            CreditOfferInitializerService initializerService = new CreditOfferInitializerService(
//                    new CreditOfferRepository(), controller);
//            initializerService.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}