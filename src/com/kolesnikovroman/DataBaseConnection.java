// Файл: src/main/java/com/kolesnikovroman/DataBaseConnection.java
package com.kolesnikovroman;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class DataBaseConnection {
    public static void main(String[] args) {
        FinancialRepository repository = new FinancialRepository();

        try {
            // --- Сводка по РАСХОДАМ за ПРОШЛЫЙ месяц ---
            System.out.println("--- Получение итогов по расходам за прошлый месяц ---");
            List<CategorySummaryDTO> expenseSummaries = repository.getLastMonthSummary();
            System.out.println("Получено категорий расходов: " + expenseSummaries.size());
            for (CategorySummaryDTO summary : expenseSummaries) {
                System.out.printf("  Категория: %s, Сумма: %.2f%n",
                        summary.getCategoryName(), summary.getTotalAmount());
            }

            // --- Сводка по ДОХОДАМ за ТЕКУЩИЙ месяц ---
            System.out.println("\n--- Получение итогов по доходам за ТЕКУЩИЙ месяц ---");
            List<CategorySummaryDTO> incomeSummaries = repository.getCurrentMonthIncomeSummary();
            System.out.println("Получено категорий доходов: " + incomeSummaries.size());
            for (CategorySummaryDTO summary : incomeSummaries) {
                System.out.printf("  Категория: %s, Сумма: %.2f%n",
                        summary.getCategoryName(), summary.getTotalAmount());
            }

            // --- Сводка по ДОХОДАМ за ПРОШЛЫЙ месяц ---
            System.out.println("\n--- Получение итогов по доходам за ПРОШЛЫЙ месяц ---");
            List<CategorySummaryDTO> lastMonthIncomes = repository.getLastMonthIncomeSummary();
            System.out.println("Получено категорий доходов: " + lastMonthIncomes.size());
            for (CategorySummaryDTO summary : lastMonthIncomes) {
                System.out.printf("  Категория: %s, Сумма: %.2f%n",
                        summary.getCategoryName(), summary.getTotalAmount());
            }

            // --- Получение кредитных предложений ---
            System.out.println("\n--- Получение всех кредитных предложений из базы ---");
            List<CreditOfferDTO> creditOffers = repository.findAllCreditOffers();
            System.out.println("Найдено предложений: " + creditOffers.size());
            for (CreditOfferDTO offer : creditOffers) {

                System.out.println("  -> " + offer);
            }
            System.out.println("\n--- Общая финансовая сводка по месяцам  ---");

            // 1. Работай пожалуйста
            List<MonthlyFinancialSummaryDTO> summaryList = repository.getMonthlyFinancialSummary();


            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLLL yyyy", new Locale("ru"));
            System.out.printf("%-18s | %12s | %12s | %12s%n", "Месяц", "Доход", "Расход", "Прибыль");
            System.out.println(new String(new char[64]).replace("\0", "-"));

            for (MonthlyFinancialSummaryDTO summary : summaryList) {
                System.out.printf("%-18s | %12.2f | %12.2f | %12.2f%n",
                        summary.getMonth().format(formatter),
                        summary.getTotalIncome(),
                        summary.getTotalExpense(),
                        summary.getProfit());
            }
        } catch (SQLException e) {
            System.err.println("Произошла ошибка при доступе к данным.");
            e.printStackTrace();
        }
    }
}