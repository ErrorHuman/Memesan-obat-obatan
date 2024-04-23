package com.example.apteca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    // Закрытый статический объект Connection
    private static Connection connection;

    // Приватный конструктор, чтобы предотвратить создание экземпляров класса
    private DataBase() {
    }

    // Метод для получения Connection
    public static synchronized Connection getConnection() {
        // Если соединение еще не инициализировано
        if (connection == null) {
            try {
                connection = createConnection();
            } catch (SQLException e) {
                // Обработка ошибки
                e.printStackTrace();
            }
        }
        return connection;
    }

    // Приватный метод для создания соединения
    private static Connection createConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/apteca";
        String user = "postgres";
        String password = "1234";

        return DriverManager.getConnection(url, user, password);
    }
}
