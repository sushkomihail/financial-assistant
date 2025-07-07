package com.paxeevamaria.logic;

import com.paxeevamaria.App;
import javafx.fxml.FXML;

public class RootController {

    private App mainApp;

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleExit() {
        mainApp.exit();
    }

    @FXML
    private void showDashboard() {
        mainApp.showDashboard();
    }

    @FXML
    private void showIncome() {
        mainApp.showIncome();
    }

    @FXML
    private void showExpense() {
        mainApp.showExpense();
    }

    @FXML
    private void showCredit() {
        mainApp.showCredits();
    }

    @FXML
    private void showSavings() {
        mainApp.showSavings();
    }
}
