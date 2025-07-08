package com.kolesnikovroman;

public class CreditOfferDTO {
    private final long id;
    private final String bankName;
    private final String amount;
    private final String rate;
    private final String term;
    private final String totalCost;

    public CreditOfferDTO(long id, String bankName, String amount, String rate, String term, String totalCost) {
        this.id = id;
        this.bankName = bankName;
        this.amount = amount;
        this.rate = rate;
        this.term = term;
        this.totalCost = totalCost;
    }

    // Геттеры
    public long getId() { return id; }
    public String getBankName() { return bankName; }
    public String getAmount() { return amount; }
    public String getRate() { return rate; }
    public String getTerm() { return term; }
    public String getTotalCost() { return totalCost; }

    @Override
    public String toString() {
        return "CreditOfferDTO{" +
                "id=" + id +
                ", bankName='" + bankName + '\'' +
                ", amount='" + amount + '\'' +
                ", rate='" + rate + '\'' +
                ", term='" + term + '\'' +
                ", totalCost='" + totalCost + '\'' +
                '}';
    }
}