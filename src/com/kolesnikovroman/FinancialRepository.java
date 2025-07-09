// Файл: src/main/java/com/kolesnikovroman/FinancialRepository.java
package com.kolesnikovroman;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinancialRepository {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/FinanceDataBase";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "12345"; // Пароль лучше хранить в конфигурации

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }



    /**
     * Вызывает функцию get_last_month_summary_by_category и возвращает список DTO.
     */
    public List<CategorySummaryDTO> getLastMonthSummary() throws SQLException {
        List<CategorySummaryDTO> summaries = new ArrayList<>();
        String sql = "SELECT * FROM get_last_month_summary_by_category();";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String category = resultSet.getString("Категория");
                BigDecimal totalAmount = resultSet.getBigDecimal("Итоговая сумма");
                summaries.add(new CategorySummaryDTO(category, totalAmount));
            }
        }
        return summaries;
    }

    /**
     * Получает расходы по названию категории.
     */
    public List<ExpenseDTO> findExpensesByCategoryName(String categoryName) throws SQLException {
        List<ExpenseDTO> expenses = new ArrayList<>();

        String sql = "SELECT e.amount, e.transaction_date, e.comment " +
                "FROM expenses e " +
                "JOIN expense_categories ec ON e.category_id = ec.id " +
                "WHERE ec.name = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, categoryName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Больше не читаем ID из ResultSet
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    LocalDate date = resultSet.getDate("transaction_date").toLocalDate();
                    String comment = resultSet.getString("comment");

                    expenses.add(new ExpenseDTO(amount, date, comment));
                }
            }
        }
        return expenses;
    }



    public List<CategorySummaryDTO> getCurrentMonthIncomeSummary() throws SQLException {
        List<CategorySummaryDTO> summaries = new ArrayList<>();
        String sql = "SELECT * FROM get_current_month_income_summary_by_category();";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String category = resultSet.getString("category_name");
                BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");
                summaries.add(new CategorySummaryDTO(category, totalAmount));
            }
        }
        return summaries;
    }


    public List<CategorySummaryDTO> getLastMonthIncomeSummary() throws SQLException {
        List<CategorySummaryDTO> summaries = new ArrayList<>();
        String sql = "SELECT * FROM get_last_month_income_summary_by_category();";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String category = resultSet.getString("category_name");
                BigDecimal totalAmount = resultSet.getBigDecimal("total_amount");
                summaries.add(new CategorySummaryDTO(category, totalAmount));
            }
        }
        return summaries;
    }


    public List<LoanOfferDTO> findAllCreditOffers() throws SQLException {
        List<LoanOfferDTO> offers = new ArrayList<>();
        // ОБНОВЛЕННЫЙ SQL: Добавили 'product_name' в список полей для выборки
        String sql = "SELECT bank_name, product_name, amount, rate, term, total_cost FROM credit_offers ORDER BY bank_name;";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                String bankName = resultSet.getString("bank_name");
                String productName = resultSet.getString("product_name"); // <-- Читаем новое поле
                String amount = resultSet.getString("amount");
                String rate = resultSet.getString("rate");
                String term = resultSet.getString("term");
                String totalCost = resultSet.getString("total_cost");


                offers.add(new LoanOfferDTO(bankName, productName, amount, rate, term, totalCost));
            }
        }
        return offers;
    }
    public List<MonthlyFinancialSummaryDTO> getMonthlyFinancialSummary() throws SQLException {
        List<MonthlyFinancialSummaryDTO> summaryList = new ArrayList<>();
        String sql = "SELECT * FROM get_full_monthly_summary();";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                LocalDate month = resultSet.getDate("month_date").toLocalDate();
                BigDecimal income = resultSet.getBigDecimal("total_income");
                BigDecimal expense = resultSet.getBigDecimal("total_expense");
                BigDecimal profit = resultSet.getBigDecimal("profit");

                summaryList.add(new MonthlyFinancialSummaryDTO(month, income, expense, profit));
            }
        }
        return summaryList;
    }
}