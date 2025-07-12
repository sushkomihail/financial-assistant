package com.kolesnikovroman;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDTO(long id, BigDecimal amount, LocalDate transactionDate, String comment, String categoryName) {
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