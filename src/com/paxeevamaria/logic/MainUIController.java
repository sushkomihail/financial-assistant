package com.paxeevamaria.logic;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

public class MainUIController {
    @FXML private PieChart expensePieChart;
    @FXML private BarChart<String, Number> incomeExpenseBarChart;
    @FXML private LineChart<String, Number> savingsChart;

    public void setMainPanel() {
        updateCharts();
    }

    private void updateCharts() {
        // Обновление данных графиков
        updateExpensePieChart();
        updateIncomeExpenseBarChart();
        updateSavingsChart();
    }

    private void updateExpensePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Еда", 45),
                new PieChart.Data("Транспорт", 20),
                new PieChart.Data("Развлечения", 15),
                new PieChart.Data("ЖКХ", 10),
                new PieChart.Data("Другое", 10)
        );
        expensePieChart.setData(pieChartData);
    }

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

    private void updateSavingsChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Накопления");
        series.getData().add(new XYChart.Data<>("Янв", 5000));
        series.getData().add(new XYChart.Data<>("Фев", 12000));
        series.getData().add(new XYChart.Data<>("Мар", 20000));
        series.getData().add(new XYChart.Data<>("Апр", 29000));

        savingsChart.getData().add(series);
    }
}