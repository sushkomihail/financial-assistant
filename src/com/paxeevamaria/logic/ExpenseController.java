package com.paxeevamaria.logic;

import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.FinancialRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseController {
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Label totalAmountLabel;
    @FXML private Label recordsCountLabel;
    @FXML private TableView<ExpenseDTO> expensesTable;
    @FXML private TableColumn<ExpenseDTO, LocalDate> dateColumn;
    @FXML private TableColumn<ExpenseDTO, String> categoryColumn;
    @FXML private TableColumn<ExpenseDTO, BigDecimal> amountColumn;
    @FXML private TableColumn<ExpenseDTO, String> commentColumn;
    @FXML private Pagination pagination;
    @FXML private ComboBox<Integer> itemsPerPageComboBox;

    private FinancialRepository financialRepository;
    private ObservableList<ExpenseDTO> allExpenses = FXCollections.observableArrayList();
    private ObservableList<String> allCategories = FXCollections.observableArrayList();
    private int itemsPerPage = 10;

    @FXML
    public void initialize() {
        financialRepository = new FinancialRepository();

        // Инициализация столбцов таблицы
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
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
        loadExpenses();
    }

    private void loadCategories() {
        try {
            List<CategorySummaryDTO> categories = financialRepository.getLastMonthSummary();
            allCategories.clear();
            allCategories.add("Все категории");
            allCategories.addAll(categories.stream()
                    .map(CategorySummaryDTO::getCategoryName)
                    .collect(Collectors.toList()));

            categoryComboBox.setItems(allCategories);
            categoryComboBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            showError("Ошибка загрузки категорий", e.getMessage());
        }
    }

    private void loadExpenses() {
        try {
            //List<ExpenseDTO> expenses = financialRepository.getAllExpenses();
            List<ExpenseDTO> expenses = Arrays.asList(
                    new ExpenseDTO(1L,
                            new BigDecimal("1500.50"),
                            LocalDate.of(2023, 5, 10),
                            "Продукты в Пятерочке"),

                    new ExpenseDTO(2L,
                            new BigDecimal("3200.00"),
                            LocalDate.of(2023, 5, 15),
                            "Оплата аренды квартиры"),

                    new ExpenseDTO(3L,
                            new BigDecimal("750.30"),
                            LocalDate.of(2023, 5, 18),
                            "Бензин на АЗС Лукойл"),

                    new ExpenseDTO(4L,
                            new BigDecimal("1200.00"),
                            LocalDate.of(2023, 6, 2),
                            "Обед в ресторане"),

                    new ExpenseDTO(5L,
                            new BigDecimal("4500.00"),
                            LocalDate.of(2023, 6, 5),
                            "Новые кроссовки")
            );
            allExpenses.setAll(expenses);

            updateTable();
            updateSummary();
            updatePagination();
        } catch (Exception e) {
            showError("Ошибка загрузки расходов", e.getMessage());
        }
//        } catch (SQLException e) {
//            showError("Ошибка загрузки расходов", e.getMessage());
//        }
    }

    @FXML
    private void handleApplyFilters() {
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        ObservableList<ExpenseDTO> filtered = allExpenses.filtered(expense -> {
            // Фильтр по категории
//            if (selectedCategory != null && !selectedCategory.equals("Все категории")
//                    && !expense.getCategoryName().equals(selectedCategory)) {
//                return false;
//            }

            // Фильтр по дате
            if (startDate != null && expense.getTransactionDate().isBefore(startDate)) {
                return false;
            }
            if (endDate != null && expense.getTransactionDate().isAfter(endDate)) {
                return false;
            }

            return true;
        });

        expensesTable.setItems(filtered);
        updateSummary();
    }

    @FXML
    private void handleResetFilters() {
        categoryComboBox.getSelectionModel().selectFirst();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        expensesTable.setItems(allExpenses);
        updateSummary();
    }

    private void updateTable() {
        int totalItems = allExpenses.size();
        int pageCount = (int) Math.ceil((double) totalItems / itemsPerPage);

        pagination.setPageCount(pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.setPageFactory(pageIndex -> {
            int fromIndex = pageIndex * itemsPerPage;
            int toIndex = Math.min(fromIndex + itemsPerPage, totalItems);
            expensesTable.setItems(FXCollections.observableArrayList(
                    allExpenses.subList(fromIndex, toIndex)));
            return expensesTable;
        });
    }

    private void updatePagination() {
        int totalItems = expensesTable.getItems().size();
        int pageCount = (int) Math.ceil((double) totalItems / itemsPerPage);
        pagination.setPageCount(pageCount > 0 ? pageCount : 1);
    }

    private void updateSummary() {
        BigDecimal total = expensesTable.getItems().stream()
                .map(ExpenseDTO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmountLabel.setText(String.format("%,.2f ₽", total));
        recordsCountLabel.setText(String.valueOf(expensesTable.getItems().size()));
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}