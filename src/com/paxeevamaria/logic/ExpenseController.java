package com.paxeevamaria.logic;

import com.kolesnikovroman.CategorySummaryDTO;
import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.FinancialRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    private FinancialRepository financialRepository;
    private ObservableList<ExpenseDTO> allExpenses = FXCollections.observableArrayList();
    private ObservableList<String> allCategories = FXCollections.observableArrayList();

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

        // Установка соотношения ширины столбцов 2:3:2:5
        dateColumn.prefWidthProperty().bind(expensesTable.widthProperty().multiply(2.0 / 12.0));
        categoryColumn.prefWidthProperty().bind(expensesTable.widthProperty().multiply(3.0 / 12.0));
        amountColumn.prefWidthProperty().bind(expensesTable.widthProperty().multiply(2.0 / 12.0));
        commentColumn.prefWidthProperty().bind(expensesTable.widthProperty().multiply(5.0 / 12.0));

        // Растягивание таблицы по высоте
        expensesTable.prefHeightProperty().bind(
                ((Region) expensesTable.getParent()).heightProperty()
        );
        loadCategories();
        loadExpenses();
    }

    private void loadCategories() {
        try {
            List<String> categories = financialRepository.findAllExpenseCategories();
            allCategories.clear();
            allCategories.add("Все категории");
            allCategories.addAll(categories);

            categoryComboBox.setItems(allCategories);
            categoryComboBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            showError("Ошибка загрузки категорий доходов", e.getMessage());
        }
    }

    private void loadExpenses() {
        try {
            List<ExpenseDTO> expenses = financialRepository.findAllExpenses();
            allExpenses.setAll(expenses);
            expensesTable.setItems(allExpenses);
            updateSummary();
        } catch (SQLException e) {
            showError("Ошибка загрузки расходов", e.getMessage());
        }
    }

    @FXML
    private void handleAddExpense() {
        try {
            // Диалог для добавления нового расхода
            Dialog<ExpenseDTO> dialog = new Dialog<>();
            dialog.setTitle("Добавить новый расход");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Создание формы
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker datePicker = new DatePicker(LocalDate.now());
            ComboBox<String> categoryCombo = new ComboBox<>(allCategories.filtered(c -> !c.equals("Все категории")));
            TextField amountField = new TextField();
            TextArea commentArea = new TextArea();

            grid.add(new Label("Дата:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Категория:"), 0, 1);
            grid.add(categoryCombo, 1, 1);
            grid.add(new Label("Сумма:"), 0, 2);
            grid.add(amountField, 1, 2);
            grid.add(new Label("Комментарий:"), 0, 3);
            grid.add(commentArea, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Преобразование результата в ExpenseDTO
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        BigDecimal amount = new BigDecimal(amountField.getText());
                        return new ExpenseDTO(0, amount, datePicker.getValue(),
                                commentArea.getText(), categoryCombo.getValue());
                    } catch (NumberFormatException e) {
                        showError("Ошибка", "Некорректная сумма");
                        return null;
                    }
                }
                return null;
            });

            Optional<ExpenseDTO> result = dialog.showAndWait();
            result.ifPresent(expense -> {
                try {
                    ExpenseDTO newExpense = financialRepository.addExpense(expense);
                    allExpenses.add(newExpense);
                    updateSummary();
                } catch (SQLException e) {
                    showError("Ошибка добавления", e.getMessage());
                }
            });

        } catch (Exception e) {
            showError("Ошибка", "Не удалось создать диалог добавления");
        }
    }

    @FXML
    private void handleEditExpense() {
        ExpenseDTO selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Ошибка", "Выберите расход для редактирования");
            return;
        }

        try {
            Dialog<ExpenseDTO> dialog = new Dialog<>();
            dialog.setTitle("Редактировать расход");

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker datePicker = new DatePicker(selected.getTransactionDate());
            ComboBox<String> categoryCombo = new ComboBox<>(allCategories.filtered(c -> !c.equals("Все категории")));
            categoryCombo.setValue(selected.getCategoryName());
            TextField amountField = new TextField(selected.getAmount().toString());
            TextArea commentArea = new TextArea(selected.getComment());

            grid.add(new Label("Дата:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Категория:"), 0, 1);
            grid.add(categoryCombo, 1, 1);
            grid.add(new Label("Сумма:"), 0, 2);
            grid.add(amountField, 1, 2);
            grid.add(new Label("Комментарий:"), 0, 3);
            grid.add(commentArea, 1, 3);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    try {
                        BigDecimal amount = new BigDecimal(amountField.getText());
                        return new ExpenseDTO(selected.getId(), amount, datePicker.getValue(),
                                commentArea.getText(), categoryCombo.getValue());
                    } catch (NumberFormatException e) {
                        showError("Ошибка", "Некорректная сумма");
                        return null;
                    }
                }
                return null;
            });

            Optional<ExpenseDTO> result = dialog.showAndWait();
            result.ifPresent(expense -> {
                try {
                    financialRepository.updateExpense(expense);
                    int index = allExpenses.indexOf(selected);
                    allExpenses.set(index, expense);
                    updateSummary();
                } catch (SQLException e) {
                    showError("Ошибка обновления", e.getMessage());
                }
            });

        } catch (Exception e) {
            showError("Ошибка", "Не удалось создать диалог редактирования");
        }
    }

    @FXML
    private void handleDeleteExpense() {
        ExpenseDTO selected = expensesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Ошибка", "Выберите расход для удаления");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Вы действительно хотите удалить выбранный расход?");
        alert.setContentText(String.format("Дата: %s\nКатегория: %s\nСумма: %,.2f ₽",
                selected.getTransactionDate(),
                selected.getCategoryName(),
                selected.getAmount()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                financialRepository.deleteExpense(selected.getId());
                allExpenses.remove(selected);
                updateSummary();
            } catch (SQLException e) {
                showError("Ошибка удаления", e.getMessage());
            }
        }
    }

    @FXML
    private void handleApplyFilters() {
        String selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        ObservableList<ExpenseDTO> filtered = allExpenses.filtered(expense -> {
            // Фильтр по категории
            if (selectedCategory != null && !selectedCategory.equals("Все категории")
                    && !expense.getCategoryName().equals(selectedCategory)) {
                return false;
            }

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