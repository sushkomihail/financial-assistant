package com.sushkomihail;

import com.sushkomihail.loan.DifferentiatedPaymentLoan;
import com.sushkomihail.loan.Loan;

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

            Loan loan = new DifferentiatedPaymentLoan(1000000, 60, 12);
            System.out.println(loan.getPayments() + ", " + loan.getPaymentsAmount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}