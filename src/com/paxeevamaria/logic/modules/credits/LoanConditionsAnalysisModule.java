package com.paxeevamaria.logic.modules.credits;

import com.kolesnikovroman.FinancialRepository;
import com.kolesnikovroman.MonthlyFinancialSummaryDTO;
import com.paxeevamaria.logic.TextFieldMask;
import com.sushkomihail.loan.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.List;

public class LoanConditionsAnalysisModule {
    private final TextField loanAmountTextField;
    private final TextField loanPeriodTextField;
    private final TextField loanInterestRateTextField;
    private final ChoiceBox<String> loanPaymentTypeChoiceBox;
    private final TextArea loanConditionsAnalysisTextArea;

    public LoanConditionsAnalysisModule(TextField loanAmountTextField,
                                        TextField loanPeriodTextField,
                                        TextField loanInterestRateTextField,
                                        ChoiceBox<String> loanPaymentTypeChoiceBox,
                                        Button loanConditionsAnalysisButton,
                                        TextArea loanConditionsAnalysisTextArea) {
        this.loanAmountTextField = loanAmountTextField;
        this.loanPeriodTextField = loanPeriodTextField;
        this.loanInterestRateTextField = loanInterestRateTextField;
        this.loanPaymentTypeChoiceBox = loanPaymentTypeChoiceBox;
        this.loanConditionsAnalysisTextArea = loanConditionsAnalysisTextArea;

        this.loanAmountTextField.setTextFormatter(TextFieldMask.INT.getTextFormatter());
        this.loanPeriodTextField.setTextFormatter(TextFieldMask.INT.getTextFormatter());
        this.loanInterestRateTextField.setTextFormatter(TextFieldMask.FLOAT.getTextFormatter());

        this.loanPaymentTypeChoiceBox.getItems().addAll(
                PaymentType.ANNUITY.getTitle(),
                PaymentType.DIFFERENTIATED.getTitle()
        );
        this.loanPaymentTypeChoiceBox.setValue(PaymentType.ANNUITY.getTitle());

        loanConditionsAnalysisButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                performAnalysis();
            }
        });
    }

    private void performAnalysis() {
        loanConditionsAnalysisTextArea.setVisible(true);
        PaymentType paymentType = PaymentType.fromString(loanPaymentTypeChoiceBox.getValue());

        if (paymentType == null) {
            return;
        }

        String loanAmountText = loanAmountTextField.getCharacters().toString();
        String loanPeriodText = loanPeriodTextField.getCharacters().toString();
        String loanInterestRateText = loanInterestRateTextField.getCharacters().toString();

        if (loanAmountText.isEmpty() || loanPeriodText.isEmpty() || loanInterestRateText.isEmpty() ||
                loanInterestRateText.equals(".")) {
            loanConditionsAnalysisTextArea.setText("Неверный формат введенных данных!");
            System.err.println("[ERROR][class LoanConditionsAnalysisModule]: Неверный формат введенных данных.");
            return;
        }

        int loanAmount = Integer.parseInt(loanAmountText);
        int loanPeriod = Integer.parseInt(loanPeriodText);
        float loanInterestRate = Float.parseFloat(loanInterestRateText);
        Loan loan;

        if (paymentType == PaymentType.DIFFERENTIATED) {
            loan = new DifferentiatedPaymentLoan(loanAmount, loanPeriod, loanInterestRate);
        } else {
            loan = new AnnuityPaymentLoan(loanAmount, loanPeriod, loanInterestRate);
        }

        try {
            LoanAnalysis analysis = new LoanAnalysis(loan, getAverageIncome());
            loanConditionsAnalysisTextArea.setText(analysis.perform());
        } catch (SQLException e) {
            loanConditionsAnalysisTextArea.setText("Не удалось получить данные о доходах!");
            System.err.println("[ERROR][class LoanConditionsAnalysisModule]: " + e.getSQLState());
        }
    }

    private float getAverageIncome() throws SQLException {
        float averageIncome = 0F;
        FinancialRepository repository = new FinancialRepository();
        List<MonthlyFinancialSummaryDTO> summaries = repository.getMonthlyFinancialSummary();

        if (!summaries.isEmpty()) {
            for (MonthlyFinancialSummaryDTO summary : summaries) {
                averageIncome += summary.getTotalIncome().intValue();
            }

            averageIncome /= summaries.size();
        }

        return averageIncome;
    }
}
