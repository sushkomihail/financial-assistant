// Файл: src/main/java/com/kolesnikovroman/DataBaseConnection.java
package com.kolesnikovroman;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DataBaseConnection {
    public static void main(String[] args) {
        System.out.println("--- ЗАПУСК ТЕСТОВ CRUD-ОПЕРАЦИЙ ---");
        try {
            // Запускаем новый, более реалистичный тест
            testUpdateAndDeleteExistingExpense();

            // Если нужно, можно добавить аналогичный тест и для доходов
            // testUpdateAndDeleteExistingIncome();

        } catch (Exception e) {
            System.err.println("\n!!! КРИТИЧЕСКАЯ ОШИБКА ВО ВРЕМЯ ТЕСТА !!!");
            e.printStackTrace();
        }
    }

    /**
     * Тестирует сценарий обновления и удаления СУЩЕСТВУЮЩЕГО расхода.

     *
     * ВАЖНО: для успешного выполнения теста в таблице 'expenses'
     * должна быть хотя бы одна запись.
     */
    public static void testUpdateAndDeleteExistingExpense() throws SQLException {
        FinancialRepository repository = new FinancialRepository();
        System.out.println("\n=========================================================");
        System.out.println("====== ТЕСТ ОБНОВЛЕНИЯ И УДАЛЕНИЯ СУЩЕСТВУЮЩЕГО РАСХОДА ======");
        System.out.println("=========================================================");

        // --- 1. READ: Получаем список расходов, чтобы выбрать цель для теста ---
        System.out.println("\n[1] READ: Получаем список всех расходов...");
        List<ExpenseDTO> initialExpenses = repository.findAllExpenses();
        printExpenses(initialExpenses);

        // Проверяем, есть ли что тестировать. Если нет, выходим.
        if (initialExpenses.isEmpty()) {
            System.out.println("\n[!] ВНИМАНИЕ: В базе данных нет расходов для тестирования. Тест UPDATE/DELETE пропущен.");
            return;
        }

        // --- 2. SELECT TARGET: Выбираем первую запись из списка как нашу цель ---
        ExpenseDTO targetExpense = initialExpenses.get(0);
        long targetId = targetExpense.getId();
        System.out.println("\n[2] TARGET: Для теста выбрана запись с id=" + targetId + " -> \"" + targetExpense.getComment() + "\"");

        // --- 3. UPDATE: Изменяем выбранную запись ---
        System.out.println("\n[3] UPDATE: Обновляем комментарий и сумму для расхода с id=" + targetId);
        ExpenseDTO expenseToUpdate = new ExpenseDTO(
                targetId, // ID остается тем же
                targetExpense.getAmount().add(new BigDecimal("100.00")), // Увеличиваем сумму на 100
                targetExpense.getTransactionDate(),
                targetExpense.getComment() + " (ОБНОВЛЕНО)", // Добавляем метку в комментарий
                targetExpense.getCategoryName() // Категорию оставляем прежней
        );
        repository.updateExpense(expenseToUpdate);
        System.out.println("  -> Запись успешно обновлена.");

        // --- 4. VERIFY UPDATE: Проверяем, что изменения применились ---
        System.out.println("\n[4] VERIFY: Проверяем список расходов после обновления...");
        List<ExpenseDTO> expensesAfterUpdate = repository.findAllExpenses();
        printExpenses(expensesAfterUpdate);

        // --- 5. DELETE: Удаляем эту же запись ---
        System.out.println("\n[5] DELETE: Удаляем расход с id=" + targetId);
        repository.deleteExpense(targetId);
        System.out.println("  -> Запись успешно удалена.");

        // --- 6. VERIFY DELETE: Проверяем финальный список ---
        System.out.println("\n[6] VERIFY: Финальная проверка списка расходов после удаления...");
        List<ExpenseDTO> finalExpenses = repository.findAllExpenses();
        printExpenses(finalExpenses);

        System.out.println("\n====== ТЕСТИРОВАНИЕ ЗАВЕРШЕНО ======");
    }

    /**
     * Вспомогательный метод для красивого вывода списка расходов в консоль.
     */
    private static void printExpenses(List<ExpenseDTO> expenses) {
        if (expenses.isEmpty()) {
            System.out.println("  -> Список расходов пуст.");
            return;
        }
        for (ExpenseDTO expense : expenses) {
            System.out.println("  -> " + expense);
        }
    }
}