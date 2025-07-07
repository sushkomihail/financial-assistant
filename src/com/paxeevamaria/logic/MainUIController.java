package com.paxeevamaria.logic;

import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.FinancialRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;

import java.util.List;

public class MainUIController {
    @FXML
    private PieChart expensePieChart; // Круговая диаграмма расходов
    @FXML
    private BarChart<String, Number> incomeExpenseBarChart; // Столбчатая диаграмма доходов/расходов
    @FXML
    private LineChart<String, Number> savingsChart; // Линейный график накоплений
    @FXML
    private ProgressIndicator pieChartLoading; // Индикатор загрузки для круговой диаграммы

    private FinancialRepository financialRepository;

    // Инициализация главной панели
    public void setMainPanel() {
        financialRepository = new FinancialRepository();
        updateCharts();
    }

    // Обновление данных графиков
    private void updateCharts() {
        updateExpensePieChart();
        updateIncomeExpenseBarChart();
        updateSavingsChart();
    }

    // Метод для обновления круговой диаграммы расходов
    private void updateExpensePieChart() {
        pieChartLoading.setVisible(true);
        expensePieChart.setVisible(false);

        // Создание задачи для загрузки данных в фоновом потоке
        Task<ObservableList<PieChart.Data>> task = new Task<>() {
            @Override
            protected ObservableList<PieChart.Data> call() throws Exception {
                List<CategorySummaryDTO> summaries = financialRepository.getLastMonthSummary();
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
        CategoryAxis xAxis = (CategoryAxis) incomeExpenseBarChart.getXAxis();
        xAxis.setLabel("Месяц");

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Доходы");
        incomeSeries.getData().add(new XYChart.Data<>("Янв", 50000));
        incomeSeries.getData().add(new XYChart.Data<>("Фев", 52000));
        incomeSeries.getData().add(new XYChart.Data<>("Мар", 48000));

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Расходы");
        expenseSeries.getData().add(new XYChart.Data<>("Янв", 35000));
        expenseSeries.getData().add(new XYChart.Data<>("Фев", 38000));
        expenseSeries.getData().add(new XYChart.Data<>("Мар", 32000));

        incomeExpenseBarChart.getData().addAll(incomeSeries, expenseSeries);
    }

    // Метод для обновления графика накоплений
    private void updateSavingsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Накопления");
        series.getData().add(new XYChart.Data<>("Янв", 5000));
        series.getData().add(new XYChart.Data<>("Фев", 12000));
        series.getData().add(new XYChart.Data<>("Мар", 20000));
        series.getData().add(new XYChart.Data<>("Апр", 29000));

        savingsChart.getData().add(series);
    }

    /**
     * Показывает диалоговое окно с сообщением об ошибке
     *
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