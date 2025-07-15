package com.paxeevamaria.logic;

import com.kolesnikovroman.ExpenseDTO;
import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.UserDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private UserDTO user;
    private FinancialRepository financialRepository;
    private final ObservableList<ExpenseDTO> allExpenses = FXCollections.observableArrayList();
    private final ObservableList<String> allCategories = FXCollections.observableArrayList();

    @FXML
    public void initialize(UserDTO user) {
        this.user = user;
        financialRepository = new FinancialRepository();

        // Инициализация столбцов таблицы
        dateColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().transactionDate()));
        categoryColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().categoryName()));
        amountColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().amount()));
        commentColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().comment()));

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

        createContextMenu();
    }

    // Создание контекстного меню
    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Изменить");
        MenuItem deleteItem = new MenuItem("Удалить");

        editItem.setOnAction(event -> handleEditExpense());
        deleteItem.setOnAction(event -> handleDeleteExpense());

        contextMenu.getItems().addAll(editItem, deleteItem);
        expensesTable.setContextMenu(contextMenu);

        // Обработчик для вызова меню по правой кнопке мыши
        expensesTable.setRowFactory(tv -> {
            TableRow<ExpenseDTO> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    expensesTable.getSelectionModel().select(row.getIndex());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });
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
            List<ExpenseDTO> expenses = financialRepository.findAllExpenses(user.id());
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
            setupDialogStyles(dialog);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream("../resources/images/add-icon.png"))));

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
                                commentArea.getText(), categoryCombo.getValue(), user.id());
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
            setupDialogStyles(dialog);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("../resources/images/add-icon.png"))));

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            DatePicker datePicker = new DatePicker(selected.transactionDate());
            ComboBox<String> categoryCombo = new ComboBox<>(allCategories.filtered(c -> !c.equals("Все категории")));
            categoryCombo.setValue(selected.categoryName());
            TextField amountField = new TextField(selected.amount().toString());
            TextArea commentArea = new TextArea(selected.comment());

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
                        return new ExpenseDTO(selected.id(), amount, datePicker.getValue(),
                                commentArea.getText(), categoryCombo.getValue(), user.id());
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
        setupDialogStyles(alert);
        alert.getDialogPane().getStyleClass().add("warning");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("../resources/images/delete-icon.png"))));
        alert.setHeaderText("Вы действительно хотите удалить выбранный расход?");
        alert.setContentText(String.format("Дата: %s\nКатегория: %s\nСумма: %,.2f ₽",
                selected.transactionDate(),
                selected.categoryName(),
                selected.amount()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                financialRepository.deleteExpense(selected.id());
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
                    && !expense.categoryName().equals(selectedCategory)) {
                return false;
            }

            // Фильтр по дате
            if (startDate != null && expense.transactionDate().isBefore(startDate)) {
                return false;
            }

            return endDate == null || !expense.transactionDate().isAfter(endDate);
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
                .map(ExpenseDTO::amount)
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

    private void setupDialogStyles(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("../resources/styles/style.css")).toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}