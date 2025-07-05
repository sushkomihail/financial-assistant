package com.kolesnikovroman; // Новый пакет для репозиториев

import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.ExpenseDTO;

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
     * @return Список объектов CategorySummaryDTO
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

                // Создаем DTO и добавляем его в список
                summaries.add(new CategorySummaryDTO(category, totalAmount));
            }
        }
        return summaries;
    }

    /**
     * Получает расходы по названию категории.
     * @param categoryName Название категории
     * @return Список объектов ExpenseDTO
     */
    public List<ExpenseDTO> findExpensesByCategoryName(String categoryName) throws SQLException {
        List<ExpenseDTO> expenses = new ArrayList<>();
        String sql = "SELECT e.id, e.amount, e.transaction_date, e.comment " +
                "FROM expenses e " +
                "JOIN expense_categories ec ON e.category_id = ec.id " +
                "WHERE ec.name = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, categoryName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    BigDecimal amount = resultSet.getBigDecimal("amount");
                    // Преобразуем java.sql.Date в java.time.LocalDate
                    LocalDate date = resultSet.getDate("transaction_date").toLocalDate();
                    String comment = resultSet.getString("comment");

                    // Создаем DTO и добавляем в список
                    expenses.add(new ExpenseDTO(id, amount, date, comment));
                }
            }
        }
        return expenses;
    }
}