// Файл: src/main/java/com/kolesnikovroman/DataBaseConnection.java
package com.kolesnikovroman;

import java.sql.SQLException;
import java.util.List;

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
                // Выводим данные через удобный метод toString()
                System.out.println("  -> " + offer);
            }

        } catch (SQLException e) {
            System.err.println("Произошла ошибка при доступе к данным.");
            e.printStackTrace();
        }
    }
}