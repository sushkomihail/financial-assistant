
package com.kolesnikovroman;

import java.math.BigDecimal;
import java.time.LocalDate;

public class IncomeDTO {
    private final long id;
    private final BigDecimal amount;
    private final LocalDate transactionDate;
    private final String comment;
    // Можно также добавить categoryId или categoryName

    public IncomeDTO(long id, BigDecimal amount, LocalDate transactionDate, String comment) {
        this.id = id;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.comment = comment;
    }

    // Геттеры
    public long getId() {
        return id;
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
                "id=" + id +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}