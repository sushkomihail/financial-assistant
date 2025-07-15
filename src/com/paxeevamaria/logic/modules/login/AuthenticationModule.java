package com.paxeevamaria.logic.modules.login;

import com.sushkomihail.login.Authentication;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public final class AuthenticationModule extends LoginMethodModule {
    public AuthenticationModule(TextField loginTextField, TextField passwordTextField, Button submitButton) {
        super(new Authentication(), loginTextField, passwordTextField, submitButton);
    }

    @Override
    public void executeMethod() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        method.execute(login, password);
    }
}
