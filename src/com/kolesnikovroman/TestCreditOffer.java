package com.kolesnikovroman;

import com.sushkomihail.llmagent.GigaChatAgent;
import com.sushkomihail.llmagent.LlmAgentController;

import java.sql.SQLException;
import java.util.List;

/**
 *Тест отладка

 */
public class TestCreditOffer {

    private static final String GIGACHAT_AUTH_KEY = "NjY2NmNhNjMtZjY2Yi00YTg3LWIwMmEtNzBkMTBjZjJhN2UwOjVkZDUzYmZiLTk3ZGYtNGY0OS1hM2I0LTE2Y2VhZDVjYzdiOA==";

    public static void main(String[] args) {
        // Выполняем инициализацию при старте приложения
        runStartupTasks();

        System.out.println("\n--- Инициализация завершена. Запуск основной логики приложения. ---");

        // Пример основной логики: вывести все предложения из базы, чтобы проверить результат
        try {
            CreditOfferRepository creditRepo = new CreditOfferRepository();
            System.out.println("\n--- Актуальный список кредитных предложений в БД: ---");
            List<LoanOfferDTO> allOffers = creditRepo.findAll();

            if (allOffers.isEmpty()) {
                System.out.println("  -> В базе данных нет кредитных предложений.");
            } else {
                allOffers.forEach(offer -> System.out.println("  -> " + offer));
            }

        } catch (SQLException e) {
            System.err.println("\n[ОСНОВНАЯ ЛОГИКА]: Ошибка при доступе к БД: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void runStartupTasks() {
        // ===== ИСПРАВЛЕННАЯ ПРОВЕРКА =====
        // Проверяем только, что ключ не является пустым или null.
        if (GIGACHAT_AUTH_KEY == null || GIGACHAT_AUTH_KEY.isBlank()) {
            System.err.println("[КРИТИЧЕСКАЯ ОШИБКА]: Не указан или пустой аутентификационный ключ GIGACHAT_AUTH_KEY. Инициализация невозможна.");
            return;
        }

        // 1. Создаем репозиторий для доступа к кредитным предложениям
        CreditOfferRepository creditRepo = new CreditOfferRepository();

        // 2. Создаем экземпляры классов для работы с LLM
        GigaChatAgent agent = new GigaChatAgent(GIGACHAT_AUTH_KEY);
        LlmAgentController llmController = new LlmAgentController(agent);

        // 3. Создаем и запускаем сервис-инициализатор, внедряя в него зависимости
        CreditOfferInitializerService initializer = new CreditOfferInitializerService(creditRepo, llmController);
        initializer.run();
    }
}