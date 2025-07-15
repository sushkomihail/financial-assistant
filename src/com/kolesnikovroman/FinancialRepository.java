package com.kolesnikovroman;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления финансовыми данными в базе данных.
 * Предоставляет полный набор CRUD-операций и методов для получения сводных отчетов.
 */
public final class FinancialRepository extends DataBaseRepository {
    // =========================================================================
    // ==== CRUD Operations for Expenses ====
    // =========================================================================

    /**
     * Добавляет новую запись о расходе в базу данных.
     * Автоматически находит ID категории по ее имени.
     *
     * @param expense DTO с данными нового расхода. Поле ID игнорируется.
     * @return Полностью сформированный DTO, включая сгенерированный базой данных ID.
     * @throws SQLException если происходит ошибка доступа к БД.
     * @throws IllegalArgumentException если указанная категория не найдена.
     */
    public ExpenseDTO addExpense(ExpenseDTO expense) throws SQLException {
        final String sql = "INSERT INTO expenses (amount, transaction_date, comment, category_id, user_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id, transaction_date";

        try (Connection connection = getConnection()) {
            long categoryId =
                    getCategoryIdByName(connection, "expense_categories", expense.categoryName())
                    .orElseThrow(() -> new IllegalArgumentException("Категория расходов '" +
                            expense.categoryName() + "' не найдена."));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBigDecimal(1, expense.amount());
                statement.setDate(2, Date.valueOf(expense.transactionDate()));
                statement.setString(3, expense.comment());
                statement.setLong(4, categoryId);
                statement.setInt(5, expense.userId());

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        long newId = rs.getLong("id");
                        LocalDate newDate = rs.getDate("transaction_date").toLocalDate();
                        return new ExpenseDTO(
                                newId, expense.amount(), newDate, expense.comment(), expense.categoryName(),
                                expense.userId());
                    } else {
                        throw new SQLException("Не удалось создать запись о расходе, ID не был получен.");
                    }
                }
            }
        }
    }

    /**
     * Обновляет существующую запись о расходе.
     *
     * @param expense DTO с обновленными данными. Поле ID используется для идентификации записи.
     * @throws SQLException если происходит ошибка доступа к БД или запись с указанным ID не найдена.
     * @throws IllegalArgumentException если указанная категория не найдена.
     */
    public void updateExpense(ExpenseDTO expense) throws SQLException {
        final String sql = "UPDATE expenses SET amount = ?, transaction_date = ?, comment = ?, category_id = ? WHERE id = ?;";

        try (Connection connection = getConnection()) {
            long categoryId = getCategoryIdByName(connection, "expense_categories", expense.categoryName())
                    .orElseThrow(() -> new IllegalArgumentException("Категория расходов '" + expense.categoryName() + "' не найдена."));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBigDecimal(1, expense.amount());
                statement.setDate(2, Date.valueOf(expense.transactionDate()));
                statement.setString(3, expense.comment());
                statement.setLong(4, categoryId);
                statement.setLong(5, expense.userId());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Обновление не удалось, расход с id=" + expense.id() + " не найден.");
                }
            }
        }
    }

    /**
     * Удаляет запись о расходе по ее идентификатору.
     *
     * @param expenseId ID расхода для удаления.
     * @throws SQLException если происходит ошибка доступа к БД или запись с указанным ID не найдена.
     */
    public void deleteExpense(long expenseId) throws SQLException {
        final String sql = "DELETE FROM expenses WHERE id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, expenseId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Удаление не удалось, расход с id=" + expenseId + " не найден.");
            }
        }
    }


    // =========================================================================
    // ==== CRUD Operations for Incomes ====
    // =========================================================================

    /**
     * Добавляет новую запись о доходе в базу данных.
     */
    public IncomeDTO addIncome(IncomeDTO income) throws SQLException {
        final String sql = "INSERT INTO incomes (amount, transaction_date, comment, category_id, user_id) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id, transaction_date;";

        try (Connection connection = getConnection()) {
            long categoryId =
                    getCategoryIdByName(connection, "income_categories", income.categoryName())
                    .orElseThrow(() -> new IllegalArgumentException("Категория доходов '" +
                            income.categoryName() + "' не найдена."));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBigDecimal(1, income.amount());
                statement.setDate(2, Date.valueOf(income.transactionDate()));
                statement.setString(3, income.comment());
                statement.setLong(4, categoryId);
                statement.setInt(5, income.userId());

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        long newId = rs.getLong("id");
                        LocalDate newDate = rs.getDate("transaction_date").toLocalDate();
                        return new IncomeDTO(
                                newId, income.amount(), newDate, income.comment(), income.categoryName(),
                                income.userId());
                    } else {
                        throw new SQLException("Не удалось создать запись о доходе, ID не был получен.");
                    }
                }
            }
        }
    }

    /**
     * Обновляет существующую запись о доходе.
     */
    public void updateIncome(IncomeDTO income) throws SQLException {
        final String sql = "UPDATE incomes SET amount = ?, transaction_date = ?, comment = ?, category_id = ? WHERE id = ?;";

        try (Connection connection = getConnection()) {
            long categoryId = getCategoryIdByName(connection, "income_categories", income.categoryName())
                    .orElseThrow(() -> new IllegalArgumentException("Категория доходов '" + income.categoryName() + "' не найдена."));

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setBigDecimal(1, income.amount());
                statement.setDate(2, Date.valueOf(income.transactionDate()));
                statement.setString(3, income.comment());
                statement.setLong(4, categoryId);
                statement.setLong(5, income.userId());

                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Обновление не удалось, доход с id=" + income.id() + " не найден.");
                }
            }
        }
    }

    /**
     * Удаляет запись о доходе по ее идентификатору.
     */
    public void deleteIncome(long incomeId) throws SQLException {
        final String sql = "DELETE FROM incomes WHERE id = ?;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, incomeId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Удаление не удалось, доход с id=" + incomeId + " не найден.");
            }
        }
    }

    // =========================================================================
    // ==== Helper Methods for Categories (for Frontend UI) ====
    // =========================================================================

    public List<String> findAllExpenseCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        final String sql = "SELECT name FROM expense_categories ORDER BY name;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    public List<String> findAllIncomeCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        final String sql = "SELECT name FROM income_categories ORDER BY name;";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("name"));
            }
        }
        return categories;
    }

    // =========================================================================
    // ==== Read-Only, Summary & Aggregate Operations ====
    // =========================================================================

    public List<ExpenseDTO> findAllExpenses(int userId) throws SQLException {
        List<ExpenseDTO> expenses = new ArrayList<>();
        final String sql = "SELECT e.id, e.amount, e.transaction_date, e.comment, ec.name AS category_name, " +
                "e.user_id FROM expenses e " +
                "JOIN expense_categories ec ON e.category_id = ec.id AND e.user_id = ? " +
                "ORDER BY e.transaction_date DESC, e.id DESC;";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                expenses.add(new ExpenseDTO(
                        resultSet.getLong("id"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getDate("transaction_date").toLocalDate(),
                        resultSet.getString("comment"),
                        resultSet.getString("category_name"),
                        resultSet.getInt("user_id")
                ));
            }
        }
        return expenses;
    }

    public List<IncomeDTO> findAllIncomes(int userId) throws SQLException {
        List<IncomeDTO> incomes = new ArrayList<>();
        final String sql = "SELECT i.id, i.amount, i.transaction_date, i.comment, ic.name AS category_name, " +
                "i.user_id FROM incomes i " +
                "JOIN income_categories ic ON i.category_id = ic.id AND i.user_id = ? " +
                "ORDER BY i.transaction_date DESC, i.id DESC;";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                incomes.add(new IncomeDTO(
                        resultSet.getLong("id"),
                        resultSet.getBigDecimal("amount"),
                        resultSet.getDate("transaction_date").toLocalDate(),
                        resultSet.getString("comment"),
                        resultSet.getString("category_name"),
                        resultSet.getInt("user_id")
                ));
            }
        }
        return incomes;
    }

    public List<CategorySummaryDTO> getLastMonthSummary(int userId) throws SQLException {
        List<CategorySummaryDTO> summaries = new ArrayList<>();
        String sql = "SELECT * FROM get_last_month_summary_by_category(?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String category = resultSet.getString("Категория");
                BigDecimal totalAmount = resultSet.getBigDecimal("Итоговая сумма");
                summaries.add(new CategorySummaryDTO(category, totalAmount));
            }
        }

        return summaries;
    }

    public List<MonthlyFinancialSummaryDTO> getMonthlyFinancialSummary(int userId) throws SQLException {
        List<MonthlyFinancialSummaryDTO> summaryList = new ArrayList<>();
        String sql = "SELECT * FROM get_full_monthly_summary(?)";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

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

    // =========================================================================
    // ==== Private Helper Methods ====
    // =========================================================================

    private Optional<Long> getCategoryIdByName(Connection connection, String categoryTable, String name) throws SQLException {
        if (!categoryTable.equals("expense_categories") && !categoryTable.equals("income_categories")) {
            throw new IllegalArgumentException("Недопустимое имя таблицы категорий.");
        }

        final String sql = "SELECT id FROM " + categoryTable + " WHERE name = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getLong("id"));
                }
            }
        }
        return Optional.empty();
    }
}