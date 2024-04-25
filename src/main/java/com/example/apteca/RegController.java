package com.example.apteca;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegController {

    @FXML
    private Text information;

    @FXML
    private Button buttonBack;

    @FXML
    private Button buttonReg;

    @FXML
    private TextField login;
    @FXML
    private TextField nameOrganization;

    @FXML
    private PasswordField password;

    @FXML
    void initialize() {
        buttonBack.setOnAction(event -> {
            if (buttonBack.getScene() != null) {
                backButton();
            } else {
                System.out.println("BackButton.getScene is null");
            }
        });

        buttonReg.setOnAction(event -> {
            if (isValidInput()) {
                try {
                    Connection connection = DataBase.getConnection();
                    String checkLoginQuery = "SELECT * FROM users WHERE name_organization = ? OR login = ?";
                    try (PreparedStatement statement = connection.prepareStatement(checkLoginQuery)) {
                        statement.setString(1, nameOrganization.getText());
                        statement.setString(2, login.getText());
                        ResultSet resultSet = statement.executeQuery();
                        if (resultSet.next()) {
                            information.setText("Пользователь с таким логином или названием организации уже существует");
                        } else {
                            String insertQuery = "INSERT INTO users (name_organization, login, password) VALUES (?, ?, ?)";
                            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                                preparedStatement.setString(1, nameOrganization.getText());
                                preparedStatement.setString(2, login.getText());
                                preparedStatement.setString(3, password.getText());
                                preparedStatement.executeUpdate();
                                information.setText("Вы успешно зарегистрированы");
                            }
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException("Ошибка при выполнении запроса", e);
                }
            }
        });
    }

    private boolean isValidInput() {
        if (nameOrganization.getText().isEmpty() || login.getText().isEmpty() || password.getText().isEmpty()) {
            information.setText("Пожалуйста, заполните все поля");
            return false;
        }
        if (!login.getText().matches("[a-zA-Z]+")) {
            information.setText("Логин должен содержать только латинские буквы");
            return false;
        }
        if (!password.getText().matches("[a-zA-Z0-9!@#$%^&*()_+-=]+")) {
            information.setText("Пароль должен содержать только латинские буквы, цифры и специальные символы");
            return false;
        }
        return true;
    }

    private void backButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("entry-scene.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 1000, 800);
            Stage currentStage = (Stage) buttonBack.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при переходе на предыдущее окно", e);
        }
    }
}
