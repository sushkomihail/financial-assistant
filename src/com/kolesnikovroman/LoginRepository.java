package com.kolesnikovroman;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoginRepository extends DataBaseRepository {
    /**
     * Получение всех пользователей из базы данных
     * @return массив данных пользователей
     */
    public List<UserDTO> getUsers() {
        List<UserDTO> users = new ArrayList<>();

        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users");
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                int id = result.getInt("id");
                String login = result.getString("login");
                String password = result.getString("password");
                users.add(new UserDTO(id, login, password));
            }
        } catch (SQLException e) {
            System.err.printf("Error %d: %s\n", e.getErrorCode(), e.getSQLState());
        }

        return users;
    }

    /**
     * Получение данных пользователя по логину
     * @param login - логин пользователя
     * @return данные пользователя
     */
    public UserDTO getUserByLogin(String login) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
            statement.setString(1, login);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                int id = result.getInt("id");
                String password = result.getString("password");
                return new UserDTO(id, login, password);
            }
        } catch (SQLException e) {
            System.err.printf("Error %d: %s\n", e.getErrorCode(), e.getSQLState());
        }

        return null;
    }

    /**
     * Добавление пользователя в базу данных
     * @param user - свойства пользователя
     */
    public void addUser(UserDTO user) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (login, password) VALUES (?, ?)");
            statement.setString(1, user.login());
            statement.setString(2, user.password());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.printf("Error %d: %s\n", e.getErrorCode(), e.getSQLState());
        }
    }
}
