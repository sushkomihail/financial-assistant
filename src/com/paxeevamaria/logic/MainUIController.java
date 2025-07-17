package com.paxeevamaria.logic;

import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.MonthlyFinancialSummaryDTO;
import com.kolesnikovroman.UserDTO;
import com.sushkomihail.llmagent.LlmAgentController;
import com.sushkomihail.datastructures.NetIncomesCollection;
import com.sushkomihail.llmagent.requests.SavingsForecastRequest;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MainUIController {
    @FXML private PieChart expensePieChart; // Круговая диаграмма расходов
    @FXML private BarChart<String, Number> incomeExpenseBarChart; // Столбчатая диаграмма доходов/расходов
    @FXML private LineChart<String, Number> savingsChart; // Линейный график накоплений
    @FXML private ProgressIndicator pieChartLoading; // Индикатор загрузки для круговой диаграммы
    @FXML private ProgressIndicator barChartLoading; // Индикатор загрузки для столбчатой диаграммы
    @FXML private ProgressIndicator lineChartLoading; // Индикатор загрузки для графика накоплений

    private UserDTO user;
    private LlmAgentController llmAgentController;
    private FinancialRepository financialRepository;

    public void setUser(UserDTO user) {
        this.user = user;
    }

    // Инициализация главной панели
    public void setMainPanel(LlmAgentController llmAgentController) {
        this.llmAgentController = llmAgentController;
        financialRepository = new FinancialRepository();
        updateCharts();
    }

    // Обновление данных графиков
    private void updateCharts() {
        updateExpensePieChart();
        updateIncomeExpenseBarChart();
        // updateSavingsChart();
    }

    // Метод для обновления круговой диаграммы расходов
    private void updateExpensePieChart() {
        pieChartLoading.setVisible(true);
        expensePieChart.setVisible(false);

        // Создание задачи для загрузки данных в фоновом потоке
        Task<ObservableList<PieChart.Data>> task = new Task<>() {
            @Override
            protected ObservableList<PieChart.Data> call() throws Exception {
                List<CategorySummaryDTO> summaries = financialRepository.getLastMonthSummary(user.id());
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                // Преобразование данных в формат для PieChart диаграммы
                for (CategorySummaryDTO summary : summaries) {
                    pieChartData.add(new PieChart.Data(
                            summary.getCategoryName(),
                            summary.getTotalAmount().doubleValue()
                    ));
                }
                return pieChartData;
            }
        };

        task.setOnSucceeded(e -> {
            expensePieChart.setData(task.getValue());
            expensePieChart.setVisible(true);
            pieChartLoading.setVisible(false);
        });

        // Действия при ошибке загрузки данных из БД
        task.setOnFailed(e -> {
            pieChartLoading.setVisible(false);
            showErrorDialog("Не удалось загрузить данные расходов");
        });

        new Thread(task).start(); // Запуск задачи в отдельном потоке
    }

    // Метод для обновления столбчатой диаграммы доходов/расходов
    private void updateIncomeExpenseBarChart() {
        incomeExpenseBarChart.getData().clear();
        incomeExpenseBarChart.setVisible(false);
        barChartLoading.setVisible(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    List<MonthlyFinancialSummaryDTO> summaries =
                            financialRepository.getMonthlyFinancialSummary(user.id());

                    if (summaries.size() > 3) {
                        summaries = summaries.subList(summaries.size() - 3, summaries.size());
                    }

                    XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
                    incomeSeries.setName("Доходы");
                    XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
                    expenseSeries.setName("Расходы");

                    String[] monthNames = {"Янв", "Фев", "Мар", "Апр", "Май", "Июн",
                            "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"};

                    ObservableList<String> categories = FXCollections.observableArrayList();

                    for (MonthlyFinancialSummaryDTO summary : summaries) {
                        String monthLabel = monthNames[summary.getMonth().getMonthValue() - 1];
                        categories.add(monthLabel);

                        incomeSeries.getData().add(new XYChart.Data<>(
                                monthLabel,
                                summary.getTotalIncome()
                        ));

                        expenseSeries.getData().add(new XYChart.Data<>(
                                monthLabel,
                                summary.getTotalExpense()
                        ));
                    }

                    Platform.runLater(() -> {
                        // Настройка осей
                        CategoryAxis xAxis = (CategoryAxis) incomeExpenseBarChart.getXAxis();
                        xAxis.setCategories(categories);
                        xAxis.setLabel("Месяц");

                        NumberAxis yAxis = (NumberAxis) incomeExpenseBarChart.getYAxis();
                        yAxis.setLabel("Сумма (руб)");

                        // Добавление данных
                        incomeExpenseBarChart.getData().addAll(incomeSeries, expenseSeries);

                        // Настройка внешнего вида
                        incomeExpenseBarChart.setCategoryGap(20);
                        incomeExpenseBarChart.setBarGap(10);

                        incomeExpenseBarChart.setVisible(true);
                        barChartLoading.setVisible(false);
                    });

                } catch (SQLException e) {
                    Platform.runLater(() -> {
                        barChartLoading.setVisible(false);
                        showErrorDialog("Не удалось загрузить данные для графика: " + e.getMessage());
                    });
                }
                return null;
            }
        };

        new Thread(task).start();
    }

    // Метод для обновления графика накоплений
    private void updateSavingsChart() {
        savingsChart.setVisible(false);
        lineChartLoading.setVisible(true);

        Task<XYChart.Series<String, Number>> task = new Task<>() {
            @Override
            protected XYChart.Series<String, Number> call() throws Exception {

                try {
                    // Получение данных о прошлых доходах и расходах из базы
                    List<MonthlyFinancialSummaryDTO> history =
                            financialRepository.getMonthlyFinancialSummary(user.id());

                    // Подготовка данных для прогноза
                    List<BigDecimal> incomes = history.stream()
                            .map(MonthlyFinancialSummaryDTO::getTotalIncome)
                            .toList();

                    List<BigDecimal> expenses = history.stream()
                            .map(MonthlyFinancialSummaryDTO::getTotalExpense)
                            .toList();

                    // Получение прогноза из LLM на 6 месяцев
                    SavingsForecastRequest request = new SavingsForecastRequest(
                            6,
                            new NetIncomesCollection(
                                    incomes.stream().map(BigDecimal::intValue).collect(Collectors.toList()),
                                    expenses.stream().map(BigDecimal::intValue).collect(Collectors.toList())
                            )
                    );

                    List<Integer> forecast = llmAgentController.getSavingsForecast(request);

                    // Формирование серии данных для графика
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Прогноз накоплений");

                    BigDecimal cumulative = BigDecimal.ZERO;
                    String[] futureMonths = getFutureMonthNames(6);
                    for (int i = 0; i < forecast.size(); i++) {
                        cumulative = cumulative.add(new BigDecimal(forecast.get(i))); // Накопление суммы
                        series.getData().add(new XYChart.Data<>(
                                futureMonths[i],
                                cumulative
                        ));
                    }

                    return series;
                } catch (SQLException e) {
                    throw new RuntimeException("Ошибка при получении данных из БД", e);
                }
            }
        };

        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                savingsChart.getData().clear();
                savingsChart.getData().add(task.getValue());

                // Настройка осей графика
                CategoryAxis xAxis = (CategoryAxis) savingsChart.getXAxis();
                xAxis.setCategories(FXCollections.observableArrayList(getFutureMonthNames(6)));
                xAxis.setLabel("Месяц");

                NumberAxis yAxis = (NumberAxis) savingsChart.getYAxis();
                yAxis.setLabel("Сумма (руб)");

                savingsChart.setVisible(true);
                lineChartLoading.setVisible(false);

                // Принудительное обновление графика
                savingsChart.requestLayout();
            });
        });

        task.setOnFailed(e -> {
            Platform.runLater(() -> {
                lineChartLoading.setVisible(false);
                showErrorDialog("Не удалось получить прогноз накоплений: " +
                        task.getException().getMessage());
            });
        });

        new Thread(task).start();
    }

    // Получение названий следующих от текущей даты 6 месяцев для отображения на графике накоплений
    private String[] getFutureMonthNames(int months) {
        String[] nextMonths = new String[months];
        LocalDate now = LocalDate.now();

        String[] nameOfMonths = {"Янв", "Фев", "Мар", "Апр", "Май", "Июн",
                "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек"};

        for (int i = 0; i < months; i++) {
            LocalDate date = now.plusMonths(i + 1);
            int monthIndex = date.getMonthValue() - 1; // Получение индекса месяца (0-11)
            nextMonths[i] = nameOfMonths[monthIndex];
        }

        return nextMonths;
    }

    /**
     * Показывает диалоговое окно с сообщением об ошибке
     * @param message Текст сообщения об ошибке
     */
    private void showErrorDialog(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Произошла ошибка");
        alert.setContentText(message);

        alert.showAndWait();
    }
}