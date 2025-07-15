package com.sushkomihail.login;

import com.kolesnikovroman.UserDTO;

import java.util.List;

public final class Registration extends LoginMethod {
    @Override
    public void execute(String login, String password) {
        if (!isRegistered(login)) {
            loginRepository.addUser(new UserDTO(0, login, password));
            user = loginRepository.getUserByLogin(login);

            if (user != null) {
                notifyObservers(true);
                return;
            }
        }

        notifyObservers(false);
    }

    private boolean isRegistered(String login) {
        List<UserDTO> users = loginRepository.getUsers();

        if (users.isEmpty()) {
            return false;
        }

        for (UserDTO user : users) {
            if (user.login().equals(login)) {
                return true;
            }
        }

        return false;
    }
}
