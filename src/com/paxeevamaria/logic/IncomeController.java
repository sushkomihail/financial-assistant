package com.paxeevamaria.logic;

import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.IncomeDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IncomeController {
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalAmountLabel;
    @FXML private Label recordsCountLabel;
    @FXML private Label averageAmountLabel;
    @FXML private TableView<IncomeDTO> incomesTable;
    @FXML private TableColumn<IncomeDTO, LocalDate> dateColumn;
    @FXML private TableColumn<IncomeDTO, String> categoryColumn;
    @FXML private TableColumn<IncomeDTO, BigDecimal> amountColumn;
    @FXML private TableColumn<IncomeDTO, String> commentColumn;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> itemsPerPageComboBox;

    private FinancialRepository financialRepository;
    private ObservableList<IncomeDTO> allIncomes = FXCollections.observableArrayList();
    private ObservableList<String> allCategories = FXCollections.observableArrayList();
    private int itemsPerPage = 10;

    @FXML
    public void initialize() {
        financialRepository = new FinancialRepository();

        // Инициализация столбцов
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        categoryColumn.setCellValueFactory(cellData -> {
            // В текущей реализации IncomeDTO нет поля categoryName, поэтому используем заглушку
            return new SimpleStringProperty("Категория");
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        commentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));

        // Формат отображения суммы
        amountColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.2f ₽", amount));
                }
            }
        });

        // Управление страницами
        itemsPerPageComboBox.getItems().addAll(5, 10, 20, 50);
        itemsPerPageComboBox.setValue(itemsPerPage);
        itemsPerPageComboBox.setOnAction(e -> {
            itemsPerPage = itemsPerPageComboBox.getValue();
            updatePagination();
        });

        loadCategories();
        loadIncomes();
    }

    private void loadCategories() {
        try {
            List<CategorySummaryDTO> categories = financialRepository.getCurrentMonthIncomeSummary();
            allCategories.clear();
            allCategories.add("Все категории");
            allCategories.addAll(categories.stream()
                    .map(CategorySummaryDTO::getCategoryName)
                    .collect(Collectors.toList()));

            categoryComboBox.setItems(allCategories);
            categoryComboBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            showError("Ошибка загрузки категорий доходов", e.getMessage());
        }
    }

    private void loadIncomes() {
        try {
            //List<IncomeDTO> incomes = financialRepository.getAllIncomes();
            List<IncomeDTO> incomes = Arrays.asList(
                    new IncomeDTO(1L,
                            new BigDecimal("1500.50"),
                            LocalDate.of(2023, 5, 10),
                            "Продукты в Пятерочке"),

                    new IncomeDTO(2L,
                            new BigDecimal("3200.00"),
                            LocalDate.of(2023, 5, 15),
                            "Оплата аренды квартиры"),

                    new IncomeDTO(3L,
                            new BigDecimal("750.30"),
                            LocalDate.of(2023, 5, 18),
                            "Бензин на АЗС Лукойл"),

                    new IncomeDTO(4L,
                            new BigDecimal("1200.00"),
                            LocalDate.of(2023, 6, 2),
                            "Обед в ресторане"),

                    new IncomeDTO(5L,
                            new BigDecimal("4500.00"),
                            LocalDate.of(2023, 6, 5),
                            "Новые кроссовки")
            );
            allIncomes.setAll(incomes);

            updateTable();
            updateSummary();
            updatePagination();
//        } catch (SQLException e) {
//            showError("Ошибка загрузки доходов", e.getMessage());
//        }
        } catch (Exception e) {
            showError("Ошибка загрузки доходов", e.getMessage());
        }
    }

    @FXML
    private void handleApplyFilters() {
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        ObservableList<IncomeDTO> filtered = allIncomes.filtered(income -> {
            // Фильтр по категории временно отключен, т.к. IncomeDTO не содержит categoryName
            // if (selectedCategory != null && !selectedCategory.equals("Все категории")) {
            //     return false;
            // }

            // Фильтр по дате
            if (startDate != null && income.getTransactionDate().isBefore(startDate)) {
                return false;
            }
            if (endDate != null && income.getTransactionDate().isAfter(endDate)) {
                return false;
            }

            return true;
        });

        incomesTable.setItems(filtered);
        updateSummary();
    }

    @FXML
    private void handleResetFilters() {
        categoryComboBox.getSelectionModel().selectFirst();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        incomesTable.setItems(allIncomes);
        updateSummary();
    }

    private void updateTable() {
        int totalItems = allIncomes.size();
        int pageCount = (int) Math.ceil((double) totalItems / itemsPerPage);

        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            incomesTable.setItems(FXCollections.observableArrayList(
                    allIncomes.subList(fromIndex, toIndex)));
            return incomesTable;
        });
    }

    private void updatePagination() {
        int totalItems = incomesTable.getItems().size();
        int pageCount = (int) Math.ceil((double) totalItems / itemsPerPage);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    }

    private void updateSummary() {
        List<IncomeDTO> currentIncomes = incomesTable.getItems();
        int count = currentIncomes.size();

        BigDecimal total = currentIncomes.stream()
                .map(IncomeDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal average = count > 0
                ? total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        totalAmountLabel.setText(String.format("%,.2f ₽", total));
        recordsCountLabel.setText(String.valueOf(count));
        averageAmountLabel.setText(String.format("%,.2f ₽", average));
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}