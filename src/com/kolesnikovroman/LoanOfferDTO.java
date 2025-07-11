package com.kolesnikovroman;

public record LoanOfferDTO(String bankName, String productName, String amount, String rate, String term,
                           String fullLoanCost) {

    @Override
    public String toString() {
        return "LoanOfferDTO {" +
                "bankName='" + bankName + '\'' +
                "productName='" + productName + '\'' +
                ", amount='" + amount + '\'' +
                ", rate='" + rate + '\'' +
                ", term='" + term + '\'' +
                ", totalCost='" + fullLoanCost + '\'' +
                '}';
    }
}