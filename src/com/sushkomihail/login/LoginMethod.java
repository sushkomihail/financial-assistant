package com.sushkomihail.login;

import com.kolesnikovroman.LoginRepository;
import com.kolesnikovroman.UserDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class LoginMethod {
    protected final LoginRepository loginRepository = new LoginRepository();
    protected List<ILoginObserver> observers = new ArrayList<>();
    protected UserDTO user;

    public UserDTO getUser() {
        return user;
    }

    public void addObserver(ILoginObserver observer) {
        observers.add(observer);
    }

    public void logout() {
        user = null;
    }

    public abstract void execute(String login, String password);

    protected void notifyObservers(boolean isLoginSuccessful) {
        for (ILoginObserver observer : observers) {
            observer.observeLoginResult(isLoginSuccessful);
        }
    }
}
