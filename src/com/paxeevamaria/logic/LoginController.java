package com.paxeevamaria.logic;

import com.paxeevamaria.logic.modules.login.AuthenticationModule;
import com.paxeevamaria.logic.modules.login.LoginMethodModule;
import com.paxeevamaria.logic.modules.login.RegistrationModule;
import com.sushkomihail.login.ILoginObserver;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginController {
    // элементы авторизации
    @FXML private VBox authBox;
    @FXML private TextField authLoginTextField;
    @FXML private TextField authPasswordTextField;
    @FXML private Button authButton;
    @FXML private Button changeToRegisterButton;

    // элементы регистрации
    @FXML private VBox registerBox;
    @FXML private TextField registerLoginTextField;
    @FXML private TextField registerPasswordTextField;
    @FXML private Button registerButton;
    @FXML private Button changeToAuthButton;

    private List<LoginMethodModule> loginMethodModules;
    private LoginMethodModule currentLoginModule;

    @FXML
    public void initialize() {
        LoginMethodModule authModule =
                new AuthenticationModule(authLoginTextField, authPasswordTextField, authButton);
        LoginMethodModule registerModule =
                new RegistrationModule(registerLoginTextField, registerPasswordTextField, registerButton);
        loginMethodModules = Arrays.asList(authModule, registerModule);
        currentLoginModule = authModule;

        changeToRegisterButton.setOnAction(actionEvent -> {
            authBox.setVisible(false);
            registerBox.setVisible(true);
            currentLoginModule = registerModule;
        });

        changeToAuthButton.setOnAction(actionEvent -> {
            registerBox.setVisible(false);
            authBox.setVisible(true);
            currentLoginModule = authModule;
        });
    }

    public void addLoginObserver(ILoginObserver observer) {
        for (LoginMethodModule module : loginMethodModules) {
            module.getMethod().addObserver(observer);
        }
    }

    public LoginMethodModule getCurrentLoginModule() {
        return currentLoginModule;
    }
}
