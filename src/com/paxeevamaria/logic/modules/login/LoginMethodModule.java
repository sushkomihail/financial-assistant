package com.paxeevamaria.logic.modules.login;

import com.sushkomihail.login.LoginMethod;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public abstract class LoginMethodModule {
    protected final LoginMethod method;
    protected final TextField loginTextField;
    protected final TextField passwordTextField;

    public LoginMethodModule(LoginMethod method, TextField loginTextField, TextField passwordTextField,
                             Button submitButton) {
        this.loginTextField = loginTextField;
        this.passwordTextField = passwordTextField;
        this.method = method;

        submitButton.setOnAction(actionEvent -> executeMethod());
    }

    public LoginMethod getMethod() {
        return method;
    }

    public abstract void executeMethod();
}
