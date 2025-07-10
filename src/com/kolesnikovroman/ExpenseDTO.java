// Файл: src/main/java/com/kolesnikovroman/ExpenseDTO.java
package com.kolesnikovroman;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDTO {
    private final long id;
    private final BigDecimal amount;
    private final LocalDate transactionDate;
    private final String comment;
    private final String categoryName;

    public ExpenseDTO(long id, BigDecimal amount, LocalDate transactionDate, String comment, String categoryName) {
        this.id = id;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.comment = comment;
        this.categoryName = categoryName;
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

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return "ExpenseDTO{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", comment='" + comment + '\'' +
                '}';
    }
}