package com.kolesnikovroman;

import com.sushkomihail.llmagent.LlmAgentController;
import com.sushkomihail.llmagent.requests.LoanOfferRequest;
import com.sushkomihail.llmagent.requests.MimeType;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CreditOfferInitializerService {

    /**
     * Конфигурация источников данных.
     * Ключ - имя банка, значение - имя файла в директории, определенной в LoanOfferRequest (res/loanoffers/).
     */
    private static final Map<String, String> BANKS_TO_PROCESS = Map.of(
            "СберБанк", "sber.pdf",
            "Альфа-Банк", "alfa.pdf",
            "ВТБ", "vtb.pdf"
    );

    private final CreditOfferRepository repository; // Используем новый репозиторий
    private final LlmAgentController llmController;

    /**
     * Конструктор, реализующий инъекцию зависимостей.
     * @param repository Репозиторий для сохранения данных о кредитных предложениях.
     * @param llmController Контроллер для взаимодействия с LLM.
     */
    public CreditOfferInitializerService(CreditOfferRepository repository, LlmAgentController llmController) {
        this.repository = repository;
        this.llmController = llmController;
    }

    /**
     * Точка входа в процесс инициализации данных.
     * Метод идемпотентен: повторные вызовы не приводят к дублированию данных.
     */
    public void run() {
        System.out.println("[Инициализатор]: Запуск синхронизации кредитных предложений...");
        try {
            // Запрашиваем из БД список банков, которые уже были обработаны
            Set<String> existingBanks = repository.getExistingBankNames();
            System.out.println("[Инициализатор]: Обнаружены данные для банков: " + existingBanks);

            // Итерируемся по списку банков, подлежащих обработке
            for (Map.Entry<String, String> entry : BANKS_TO_PROCESS.entrySet()) {
                String bankName = entry.getKey();
                String fileName = entry.getValue();

                // Пропускаем банки, данные по которым уже есть в системе
                if (existingBanks.contains(bankName)) {
                    System.out.printf("  -> Банк '%s' уже синхронизирован. Пропуск.\n", bankName);
                    continue;
                }

                // Запускаем обработку для нового банка
                System.out.printf("  -> Обнаружен новый банк '%s'. Запуск обработки файла '%s'...\n", bankName, fileName);
                processNewBank(bankName, fileName);
            }

        } catch (SQLException e) {
            System.err.println("[Инициализатор]: Критическая ошибка при работе с базой данных. Процесс остановлен.");
            e.printStackTrace();
        }
        System.out.println("[Инициализатор]: Синхронизация завершена.");
    }

    /**
     * Инкапсулирует логику обработки одного банка: вызов LLM и сохранение результата.
     * @param bankName Имя банка.
     * @param fileName Имя файла для парсинга.
     */
    private void processNewBank(String bankName, String fileName) {
        try {
            LoanOfferRequest request = new LoanOfferRequest(MimeType.PDF, fileName);

            System.out.println("    [LLM]: Отправка запроса к нейросети...");
            List<LoanOfferDTO> offersFromAI = llmController.getLoanOffers(bankName, request);

            if (offersFromAI == null || offersFromAI.isEmpty()) {
                System.out.println("    [LLM]: Нейросеть не вернула предложений для банка '" + bankName + "'.");
                return;
            }

            System.out.printf("    [LLM]: Получено %d предложений. Сохранение в БД...\n", offersFromAI.size());

            for (LoanOfferDTO offer : offersFromAI) {
                repository.add(offer);
                System.out.printf("      [OK] Сохранено: '%s'\n", offer.productName());
            }
            System.out.printf("    [БД]: Все предложения для банка '%s' успешно сохранены.\n", bankName);

        } catch (Exception e) {
            System.err.printf("    [КРИТИЧЕСКАЯ ОШИБКА] при обработке банка '%s': %s\n", bankName, e.getMessage());
            e.printStackTrace();
        }
    }
}