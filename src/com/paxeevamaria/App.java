package com.paxeevamaria;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    private LlmAgentController llmAgentController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Финансовый ассистент");
        this.primaryStage.setMaximized(true);

        initRootLayout();
        showDashboard();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/root.fxml"));
            rootLayout = loader.load();

            RootController controller = loader.getController();
            controller.setMainApp(this);

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

//          --------------- GIGA CHAT -----------------
//            Properties props = new Properties();
//            FileInputStream configFile = new FileInputStream("gigachatapi.properties");
//            props.load(configFile);
//            String authKey = props.getProperty("auth_key");
//
//            GigaChatAgent gigaChatAgent = new GigaChatAgent(authKey);
//            LlmAgentController llmAgentController = new LlmAgentController(gigaChatAgent);

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
            controller.initialize();
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
            controller.initialize();

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
            controller.initialize(this.llmAgentController);

        } catch (IOException e) {
            e.printStackTrace();
//            showAlert("Ошибка", "Не удалось загрузить кредитные предложения", e.getMessage());
        }
    }
   // public void showCredits() {}

    public void showSavings() {}

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
