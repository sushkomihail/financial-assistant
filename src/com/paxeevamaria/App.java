package com.paxeevamaria;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import com.kolesnikovroman.UserDTO;
import com.paxeevamaria.logic.*;
import com.sushkomihail.llmagent.GigaChatAgent;
import com.sushkomihail.llmagent.LlmAgentController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootController rootController;
    private LoginController loginController;
    private LlmAgentController llmAgentController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Финансовый ассистент");
        this.primaryStage.setMaximized(true);

        try {
            Image icon = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("resources/images/app-icon.png")));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Не удалось загрузить иконку приложения: " + e.getMessage());
        }

        showRootLayout();
        showLoginPane();
    }

    public LoginController getLoginController() {
        return loginController;
    }

    public UserDTO getUser() {
        return loginController.getCurrentLoginModule().getMethod().getUser();
    }

    public void showLoginPane() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/Login.fxml"));
            AnchorPane loginPane = loader.load();

            rootLayout.setCenter(loginPane);

            loginController = loader.getController();
            loginController.initialize();
            loginController.addLoginObserver(rootController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/Root.fxml"));
            rootLayout = loader.load();

            rootController = loader.getController();
            rootController.initialize(this);

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/MainUI.fxml"));
            AnchorPane dashboard = loader.load();

            rootLayout.setCenter(dashboard);
            MainUIController controller = loader.getController();
            controller.setUser(getUser());

            initGigaChat();
            controller.setMainPanel(llmAgentController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        // Диалог подтверждения перед выходом
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение выхода");
        alert.setHeaderText("Вы уверены, что хотите выйти?");
        alert.setContentText("Все несохраненные данные будут потеряны.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    public void showIncome() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/Income.fxml"));
            AnchorPane incomePanel = loader.load();

            rootLayout.setCenter(incomePanel);

            IncomeController controller = loader.getController();
            controller.initialize(getUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showExpense() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/Expense.fxml"));
            AnchorPane expensePanel = loader.load();

            rootLayout.setCenter(expensePanel);

            ExpenseController controller = loader.getController();
            controller.initialize(getUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCredits() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/Credits.fxml"));
            ScrollPane creditsPanel = loader.load();

            rootLayout.setCenter(creditsPanel);

            CreditsController controller = loader.getController();
            controller.initialize(this.llmAgentController, getUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGigaChat() throws IOException {
        Properties props = new Properties();
        FileInputStream configFile = new FileInputStream("gigachatapi.properties");
        props.load(configFile);
        String authKey = props.getProperty("auth_key");

        GigaChatAgent gigaChatAgent = new GigaChatAgent(authKey);
        this.llmAgentController = new LlmAgentController(gigaChatAgent);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
