package com.paxeevamaria.logic;

import com.kolesnikovroman.CreditOfferInitializerService;
import com.kolesnikovroman.CreditOfferRepository;
import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.LoanOfferDTO;
import com.paxeevamaria.logic.modules.credits.LoanConditionsAnalysisModule;
import com.sushkomihail.llmagent.LlmAgentController;
import com.sushkomihail.llmagent.requests.LoanOffersRequest;
import com.sushkomihail.llmagent.requests.MimeType;
import com.sushkomihail.loan.PaymentType;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
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

    // Для анализа кредитных условий
    @FXML private TextField loanAmountTextField;
    @FXML private TextField loanPeriodTextField;
    @FXML private TextField loanInterestRateTextField;
    @FXML private ChoiceBox<String> loanPaymentTypeChoiceBox;
    @FXML private Button loanConditionsAnalysisButton;
    @FXML private TextArea loanConditionsAnalysisTextArea;

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

        // Инициализация модуля анализа кредитных условий
        loanConditionsAnalysisTextArea.setVisible(false);
        LoanConditionsAnalysisModule loanConditionsAnalysisModule = new LoanConditionsAnalysisModule(
                loanAmountTextField,
                loanPeriodTextField,
                loanInterestRateTextField,
                loanPaymentTypeChoiceBox,
                loanConditionsAnalysisButton,
                loanConditionsAnalysisTextArea
        );

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

    // Обработка загрузки файла с кредитными предложениями
    @FXML
    private void handleUploadDocument() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите документ");

        // Установка фильтров для расширений файлов
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Все файлы", "*.*"),
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("PDF документы", "*.pdf"),
                new FileChooser.ExtensionFilter("Документы Word", "*.docx", "*.doc"),
                new FileChooser.ExtensionFilter("Электронные книги ", "*.epub"),
                new FileChooser.ExtensionFilter("Презентация", "*.ppt", "*.pptx")
        );

        // Показ диалога выбора файла
        File selectedFile = fileChooser.showOpenDialog(creditsTable.getScene().getWindow());

        if (selectedFile != null) {
            // Абсолютный путь к файлу
            String absolutePath = selectedFile.getAbsolutePath();
            showInformationAlert("Файл загружен", "Выбран файл: " + selectedFile.getName());

            // Создание Task для асинхронной обработки файла
            Task<List<LoanOfferDTO>> uploadTask = new Task<>() {
                @Override
                protected List<LoanOfferDTO> call() throws Exception {
                    return llmAgentController.getLoanOffers(
                            null, new LoanOffersRequest(MimeType.PDF, absolutePath, true));
                }
            };

            uploadTask.setOnSucceeded(e -> {
                // Результат обработки файла
                List<LoanOfferDTO> newOffers = uploadTask.getValue()
                        .stream()
                        .map(offer -> new LoanOfferDTO(
                                "Банк",  // Заглушка
                                offer.productName(),
                                offer.amount(),
                                offer.rate(),
                                offer.term(),
                                offer.fullLoanCost()
                        ))
                        .toList();
                // Добавление новых предложений в таблицу
                offersData.addAll(newOffers);

                showInformationAlert("Успех", "Добавлено " + newOffers.size() + " новых предложений");
            });

            uploadTask.setOnFailed(e -> {
                showAlert("Ошибка", "Не удалось обработать файл",
                        uploadTask.getException().getMessage());
            });

            new Thread(uploadTask).start();
        }
    }

    private void showInformationAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}