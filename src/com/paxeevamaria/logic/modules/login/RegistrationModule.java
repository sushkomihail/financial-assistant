package com.paxeevamaria.logic.modules.login;

import com.sushkomihail.login.Registration;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public final class RegistrationModule extends LoginMethodModule {
    public RegistrationModule(TextField loginTextField, TextField passwordTextField, Button submitButton) {
        super(new Registration(), loginTextField, passwordTextField, submitButton);
    }

    @Override
    public void executeMethod() {
        String login = loginTextField.getText();
        String password = passwordTextField.getText();
        method.execute(login, password);
    }
}
