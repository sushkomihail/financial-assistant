package com.paxeevamaria.logic;

import com.paxeevamaria.App;
import com.sushkomihail.login.ILoginObserver;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;

public class RootController implements ILoginObserver {
    @FXML private Menu userMenu;

    private App app;

    public void initialize(App app) {
        this.app = app;
    }

    @FXML
    private void handleExit() {
        app.exit();
    }

    @FXML
    private void handleLogout() {
        app.getLoginController().getCurrentLoginModule().getMethod().logout();
        app.showLoginPane();
        userMenu.setText("Пользователь: не авторизован");
    }

    @FXML
    private void showDashboard() {
        if (app.getUser() == null) {
            return;
        }

        app.showDashboard();
    }

    @FXML
    private void showIncome() {
        if (app.getUser() == null) {
            return;
        }

        app.showIncome();
    }

    @FXML
    private void showExpense() {
        if (app.getUser() == null) {
            return;
        }

        app.showExpense();
    }

    @FXML
    private void showCredit() {
        if (app.getUser() == null) {
            return;
        }

        app.showCredits();
    }

    @Override
    public void observeLoginResult(boolean isSuccessful) {
        if (isSuccessful) {
            userMenu.setText(String.format("Пользователь: %s", app.getUser().login()));
            showDashboard();
        } else {
            userMenu.setText("Пользователь: не авторизован");
        }
    }
}
