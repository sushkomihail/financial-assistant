package com.kolesnikovroman;

public record UserDTO(int id, String login, String password) {
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
