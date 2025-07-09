// Файл: src/main/java/com/kolesnikovroman/IncomeDTO.java
package com.kolesnikovroman;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeDTO {

    private final BigDecimal amount;
    private final LocalDate transactionDate;
    private final String comment;


    public IncomeDTO(BigDecimal amount, LocalDate transactionDate, String comment) {
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.comment = comment;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {

        return "IncomeDTO{" +
                "amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}