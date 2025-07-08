package com.kolesnikovroman;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO для хранения полной финансовой сводки за месяц:
 * доходы, расходы и чистая прибыль.

 */
public class MonthlyFinancialSummaryDTO {
    private final LocalDate month;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal profit;

    public MonthlyFinancialSummaryDTO(LocalDate month, BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal profit) {
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.profit = profit;
    }

    // Геттеры
    public LocalDate getMonth() { return month; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public BigDecimal getProfit() { return profit; }

    @Override
    public String toString() {
        return "MonthlyFinancialSummaryDTO{" +
                "month=" + month +
                ", totalIncome=" + totalIncome +
                ", totalExpense=" + totalExpense +
                ", profit=" + profit +
                '}';
    }
}