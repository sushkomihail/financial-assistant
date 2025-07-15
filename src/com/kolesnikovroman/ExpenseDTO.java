package com.kolesnikovroman;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseDTO(long id, BigDecimal amount, LocalDate transactionDate, String comment, String categoryName,
                         int userId) {
    @Override
    public String toString() {
        return "ExpenseDTO{" +
                "id=" + id +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", comment='" + comment + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", userId=" + userId +
                '}';
    }
}