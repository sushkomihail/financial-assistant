package com.sushkomihail.loan;

public enum PaymentType {
    ANNUITY("Аннуитетный"),
    DIFFERENTIATED("Дифференцированный");

    private final String title;

    PaymentType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
