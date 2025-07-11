package com.paxeevamaria.logic;

import com.kolesnikovroman.CreditOfferInitializerService;
import com.kolesnikovroman.CreditOfferRepository;
import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.LoanOfferDTO;
import com.sushkomihail.llmagent.LlmAgentController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class CreditsController {

    @FXML private TableView<LoanOfferDTO> creditsTable;
    @FXML private TableColumn<LoanOfferDTO, String> bankColumn;
    @FXML private TableColumn<LoanOfferDTO, String> productColumn;
    @FXML private TableColumn<LoanOfferDTO, String> amountColumn;
    @FXML private TableColumn<LoanOfferDTO, String> rateColumn;
    @FXML private TableColumn<LoanOfferDTO, String> termColumn;
    @FXML private TableColumn<LoanOfferDTO, String> totalCostColumn;

    @FXML private VBox recommendationBox;
    @FXML private TextArea recommendationText;

    private LlmAgentController llmAgentController;
    private CreditOfferRepository creditOfferRepository;
    private ObservableList<LoanOfferDTO> offersData = FXCollections.observableArrayList();

    public void initialize(LlmAgentController llmAgentController) {
        this.llmAgentController = llmAgentController;
        this.creditOfferRepository = new CreditOfferRepository();
        CreditOfferInitializerService initializerService = new CreditOfferInitializerService(
                creditOfferRepository, llmAgentController);
        initializerService.run();

        // Настройка колонок таблицы
        bankColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().bankName()));
        productColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().productName()));
        amountColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().amount()));
        rateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().rate()));
        termColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().term()));
        totalCostColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fullLoanCost()));

        loadCreditOffers();
    }

    private void loadCreditOffers() {
        Task<List<LoanOfferDTO>> loadTask = new Task<>() {
            @Override
            protected List<LoanOfferDTO> call() throws Exception {
                return creditOfferRepository.findAll();
            }
        };

        loadTask.setOnSucceeded(e -> {
            offersData.setAll(loadTask.getValue());
            creditsTable.setItems(offersData);
        });

        loadTask.setOnFailed(e -> {
            showAlert("Ошибка", "Не удалось загрузить кредитные предложения",
                    loadTask.getException().getMessage());
        });

        new Thread(loadTask).start();
    }

    @FXML
    private void handleRefresh() {
        loadCreditOffers();
        recommendationBox.setVisible(false);
    }

    @FXML
    private void handleGetRecommendation() {
        if (creditsTable.getSelectionModel().isEmpty()) {
            showAlert("Ошибка", "Не выбрано предложение",
                    "Пожалуйста, выберите кредитное предложение из таблицы");
            return;
        }

        LoanOfferDTO selected = creditsTable.getSelectionModel().getSelectedItem();
        String prompt = String.format(
                "Проанализируй кредитное предложение: банк '%s', продукт '%s', сумма %s, ставка %s, срок %s, общая стоимость %s. " +
                        "Дай краткую оценку этому предложению (1-2 предложения) и рекомендацию - стоит ли брать этот кредит.",
                selected.bankName(), selected.productName(), selected.amount(),
                selected.rate(), selected.term(), selected.fullLoanCost());

        Task<String> recommendationTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                return llmAgentController.getResponse(prompt);
            }
        };

        recommendationTask.setOnSucceeded(e -> {
            recommendationText.setText(recommendationTask.getValue());
            recommendationBox.setVisible(true);
        });

        recommendationTask.setOnFailed(e -> {
            showAlert("Ошибка", "Не удалось получить рекомендацию",
                    recommendationTask.getException().getMessage());
        });

        new Thread(recommendationTask).start();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}