package com.paxeevamaria.logic;

import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.IncomeDTO;
import com.kolesnikovroman.UserDTO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    private UserDTO user;
    private FinancialRepository financialRepository;
    private final ObservableList<IncomeDTO> allIncomes = FXCollections.observableArrayList();
    private final ObservableList<String> allCategories = FXCollections.observableArrayList();

    @FXML
    public void initialize(UserDTO user) {
        this.user = user;
        financialRepository = new FinancialRepository();

        // Инициализация столбцов
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
        dateColumn.prefWidthProperty().bind(incomesTable.widthProperty().multiply(2.0 / 12.0));
        categoryColumn.prefWidthProperty().bind(incomesTable.widthProperty().multiply(3.0 / 12.0));
        amountColumn.prefWidthProperty().bind(incomesTable.widthProperty().multiply(2.0 / 12.0));
        commentColumn.prefWidthProperty().bind(incomesTable.widthProperty().multiply(5.0 / 12.0));

        // Растягивание таблицы по высоте
        incomesTable.prefHeightProperty().bind(
                ((Region) incomesTable.getParent()).heightProperty()
        );
        loadCategories();
        loadIncomes();

        createContextMenu();
    }

    // Создание контекстного меню
    private void createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("Изменить");
        MenuItem deleteItem = new MenuItem("Удалить");

        editItem.setOnAction(event -> handleEditIncome());
        deleteItem.setOnAction(event -> handleDeleteIncome());

        contextMenu.getItems().addAll(editItem, deleteItem);
        incomesTable.setContextMenu(contextMenu);

        // Обработчик для вызова меню по правой кнопке мыши
        incomesTable.setRowFactory(tv -> {
            TableRow<IncomeDTO> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    incomesTable.getSelectionModel().select(row.getIndex());
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            return row;
        });
    }

    private void loadCategories() {
        try {
            List<String> categories = financialRepository.findAllIncomeCategories();
            allCategories.clear();
            allCategories.add("Все категории");
            allCategories.addAll(categories);

            categoryComboBox.setItems(allCategories);
            categoryComboBox.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            showError("Ошибка загрузки категорий доходов", e.getMessage());
        }
    }

    private void loadIncomes() {
        try {
            List<IncomeDTO> incomes = financialRepository.findAllIncomes(user.id());
            allIncomes.setAll(incomes);
            incomesTable.setItems(allIncomes);
            updateSummary();
        } catch (SQLException e) {
            showError("Ошибка загрузки доходов", e.getMessage());
        }
    }

    @FXML
    private void handleAddIncome() {
        Dialog<IncomeDTO> dialog = new Dialog<>();
        dialog.setTitle("Добавить новый доход");
        setupDialogStyles(dialog);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("../resources/images/add-icon.png"))));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

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

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    return new IncomeDTO(0, amount, datePicker.getValue(),
                            commentArea.getText(), categoryCombo.getValue(), user.id());
                } catch (NumberFormatException e) {
                    showError("Ошибка", "Некорректная сумма");
                    return null;
                }
            }
            return null;
        });

        Optional<IncomeDTO> result = dialog.showAndWait();
        result.ifPresent(income -> {
            try {
                IncomeDTO newIncome = financialRepository.addIncome(income);
                allIncomes.add(newIncome);
                updateSummary();
            } catch (SQLException e) {
                showError("Ошибка добавления", e.getMessage());
            }
        });
    }

    @FXML
    private void handleEditIncome() {
        IncomeDTO selected = incomesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Ошибка", "Выберите доход для редактирования");
            return;
        }

        Dialog<IncomeDTO> dialog = new Dialog<>();
        dialog.setTitle("Редактировать доход");
        setupDialogStyles(dialog);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("../resources/images/add-icon.png"))));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

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
                    return new IncomeDTO(selected.id(), amount, datePicker.getValue(),
                            commentArea.getText(), categoryCombo.getValue(), user.id());
                } catch (NumberFormatException e) {
                    showError("Ошибка", "Некорректная сумма");
                    return null;
                }
            }
            return null;
        });

        Optional<IncomeDTO> result = dialog.showAndWait();
        result.ifPresent(income -> {
            try {
                financialRepository.updateIncome(income);
                int index = allIncomes.indexOf(selected);
                allIncomes.set(index, income);
                updateSummary();
            } catch (SQLException e) {
                showError("Ошибка обновления", e.getMessage());
            }
        });
    }

    @FXML
    private void handleDeleteIncome() {
        IncomeDTO selected = incomesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Ошибка", "Выберите доход для удаления");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        setupDialogStyles(alert);
        alert.getDialogPane().getStyleClass().add("warning");
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("../resources/images/delete-icon.png"))));
        alert.setHeaderText("Вы действительно хотите удалить выбранный доход?");
        alert.setContentText(String.format("Дата: %s\nКатегория: %s\nСумма: %,.2f ₽",
                selected.transactionDate(),
                selected.categoryName(),
                selected.amount()));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                financialRepository.deleteIncome(selected.id());
                allIncomes.remove(selected);
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

        ObservableList<IncomeDTO> filtered = allIncomes.filtered(income -> {
            if (selectedCategory != null && !selectedCategory.equals("Все категории")
                    && !income.categoryName().equals(selectedCategory)) {
                return false;
            }

            if (startDate != null && income.transactionDate().isBefore(startDate)) {
                return false;
            }

            return endDate == null || !income.transactionDate().isAfter(endDate);
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

    private void updateSummary() {
        List<IncomeDTO> currentIncomes = incomesTable.getItems();
        int count = currentIncomes.size();

        BigDecimal total = currentIncomes.stream()
                .map(IncomeDTO::amount)
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

    private void setupDialogStyles(Dialog<?> dialog) {
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource("../resources/styles/style.css")).toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
    }
}