package com.example.apteca;

// Сеанс пользвателя
public class UserSession {
    private static String loggedInUsername;

    // Установка исень пользователь в системе
    public static void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    // Метод получения пользователя, который вошел в систему
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }
}
