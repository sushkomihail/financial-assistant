package com.sushkomihail.login;

import com.kolesnikovroman.UserDTO;

import java.util.List;

public final class Authentication extends LoginMethod {
    @Override
    public void execute(String login, String password) {
        UserDTO user = tryGetUserFromDataBase(login, password);

        if (user != null) {
            this.user = user;
            notifyObservers(true);
        } else {
            notifyObservers(false);
        }
    }

    private UserDTO tryGetUserFromDataBase(String login, String password) {
        List<UserDTO> users = loginRepository.getUsers();

        if (users.isEmpty()) {
            return null;
        }

        for (UserDTO user : users) {
            if (user.login().equals(login) && user.password().equals(password)) {
                return user;
            }
        }

        return null;
    }
}
