package com.kolesnikovroman;
import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.FinancialRepository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class DataBaseConnection {
    public static void main(String[] args) {
        FinancialRepository repository = new FinancialRepository();

        try {
            System.out.println("--- Получение итогов за прошлый месяц ---");
            // Получаем готовый список объектов
            List<CategorySummaryDTO> summaries = repository.getLastMonthSummary();

            // Теперь Маша может взять эту переменную 'summaries'
            // и, например, преобразовать ее в JSON
            System.out.println("Получено объектов: " + summaries.size());
            for (CategorySummaryDTO summary : summaries) {

                System.out.printf("Категория: %s, Сумма: %.2f%n",
                        summary.getCategoryName(), summary.getTotalAmount());
            }

            System.out.println("\n--- Поиск расходов в категории 'Продукты' ---");
            List<ExpenseDTO> foodExpenses = repository.findExpensesByCategoryName("Продукты");

            System.out.println("Найдено расходов: " + foodExpenses.size());
            for (ExpenseDTO expense : foodExpenses) {
                // Выводим данные через метод toString(), который мы определили в DTO
                System.out.println("  -> " + expense);
            }

        } catch (SQLException e) {
            System.err.println("Произошла ошибка при доступе к данным.");
            e.printStackTrace();
        }

    }
}
