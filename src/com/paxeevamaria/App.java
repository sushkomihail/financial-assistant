package com.paxeevamaria;

import java.io.IOException;
import com.paxeevamaria.logic.IncomeController;
import com.paxeevamaria.logic.MainUIController;
import com.paxeevamaria.logic.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Финансовый ассистент");

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
            controller.setMainPanel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {}

    public void showIncome() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("./resources/views/Income.fxml"));
            AnchorPane incomePanel = loader.load();

            rootLayout.setCenter(incomePanel);

            IncomeController controller = loader.getController();
            controller.setMainPanel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showExpense() {}

    public void showAnalytics() {}

    public void showCredit() {}

    public void showSavings() {}

    public static void main(String[] args) {
        launch(args);
    }
}
