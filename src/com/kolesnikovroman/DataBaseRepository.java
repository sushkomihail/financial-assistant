package com.kolesnikovroman;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DataBaseRepository {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/FinanceDataBase";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "12345";

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
