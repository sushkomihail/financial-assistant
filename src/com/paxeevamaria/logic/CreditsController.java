package com.paxeevamaria.logic;

import com.kolesnikovroman.LoanOfferDTO;
import com.sushkomihail.llmagent.LlmAgentController;
import com.sushkomihail.llmagent.requests.LoanOfferRequest;
import com.sushkomihail.llmagent.requests.MimeType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class CreditsController {
    @FXML
    private ComboBox<String> bankComboBox;
    @FXML
    private VBox offersContainer;

    private LlmAgentController llmAgentController;

    public void initialize(LlmAgentController llmAgentController) {
        this.llmAgentController = llmAgentController;

        // Инициализация выпадающего списка банков
        ObservableList<String> banks = FXCollections.observableArrayList(
                "Все банки",
                "Сбербанк",
                "ВТБ",
                "Альфа-Банк"
        );
        bankComboBox.setItems(banks);
        bankComboBox.getSelectionModel().selectFirst();

        // Загрузка предложений по умолчанию (все банки)
        loadOffers(null);
    }

    @FXML
    private void handleRefresh() {
        String selectedBank = bankComboBox.getSelectionModel().getSelectedItem();
        String bankFileName = null;

        if ("Сбербанк".equals(selectedBank)) {
            bankFileName = "sber.pdf";
        } else if ("ВТБ".equals(selectedBank)) {
            bankFileName = "vtb.pdf";
        } else if ("Альфа-Банк".equals(selectedBank)) {
            bankFileName = "alfa.pdf";
        }

        loadOffers(bankFileName);
    }

    private void loadOffers(String bankFileName) {
        offersContainer.getChildren().clear();

        try {
            List<LoanOfferDTO> allOffers = new ArrayList<>();

            if (bankFileName == null) {
                // Загрузка предложений из всех банков
                allOffers.addAll(loadBankOffers("sber.pdf"));
                allOffers.addAll(loadBankOffers("vtb.pdf"));
                allOffers.addAll(loadBankOffers("alfa.pdf"));
            } else {
                // Загрузка предложений только выбранного банка
                allOffers.addAll(loadBankOffers(bankFileName));
            }

            if (allOffers.isEmpty()) {
                Label noOffersLabel = new Label("Нет доступных кредитных предложений");
                offersContainer.getChildren().add(noOffersLabel);
            } else {
                for (LoanOfferDTO offer : allOffers) {
                    addOfferToContainer(offer);
                }
            }
        } catch (Exception e) {
            Label errorLabel = new Label("Ошибка при загрузке кредитных предложений: " + e.getMessage());
            offersContainer.getChildren().add(errorLabel);
        }
    }

    private List<LoanOfferDTO> loadBankOffers(String bankFileName) {
        try {
            LoanOfferRequest request = new LoanOfferRequest(MimeType.PDF, bankFileName);
            return llmAgentController.getLoanOffers(null, request);
        } catch (Exception e) {
            System.err.println("Ошибка при загрузке предложений из файла " + bankFileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void addOfferToContainer(LoanOfferDTO offer) {
        VBox offerBox = new VBox(5);
        offerBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label bankLabel = new Label("Банк: " + offer.bankName());
        bankLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label productLabel = new Label("Продукт: " + offer.productName());
        Label amountLabel = new Label("Сумма: " + offer.amount());
        Label rateLabel = new Label("Ставка: " + offer.rate());
        Label termLabel = new Label("Срок: " + offer.term());
        Label costLabel = new Label("Полная стоимость кредита: " + offer.fullLoanCost());

        offerBox.getChildren().addAll(
                bankLabel,
                productLabel,
                amountLabel,
                rateLabel,
                termLabel,
                costLabel
        );

        offersContainer.getChildren().add(offerBox);
    }
}