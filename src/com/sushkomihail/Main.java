package com.sushkomihail;

import com.sushkomihail.loan.AnnuityPaymentLoan;
import com.sushkomihail.loan.DifferentiatedPaymentLoan;
import com.sushkomihail.loan.Loan;
import com.sushkomihail.loan.LoanAnalytics;

import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            FileInputStream configFile = new FileInputStream("gigachatapi.properties");
            props.load(configFile);
            String authKey = props.getProperty("auth_key");
            // GigaChatAgent gigaChatModel = new GigaChatAgent(authKey);

//            System.out.println(gigaChatModel.handleRequest(
//                    new SavingsForecastRequest(3, 100000,
//                            "одежда - 5000, жкх - 10000, транспорт - 5000, продукты - 10000")));

            LoanAnalytics analytics =
                    new LoanAnalytics(new DifferentiatedPaymentLoan(1000000, 60, 12), 100000);
            System.out.println(analytics.generate());
            analytics =
                    new LoanAnalytics(new AnnuityPaymentLoan(1000000, 60, 12), 10000);
            System.out.println();
            System.out.println();
            System.out.println(analytics.generate());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}