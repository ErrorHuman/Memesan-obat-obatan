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

public class EntryController {

    @FXML
    private Text information;

    @FXML
    private Button buttonIn;

    @FXML
    private Button buttonReg;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    void initialize() {
        buttonReg.setOnAction(event -> openRegistrationWindow());
        buttonIn.setOnAction(event -> openUserWindow());
    }

    @FXML
    private void openRegistrationWindow() {
        loadWindow("regScene.fxml", buttonReg);
    }

    private void openUserWindow() {
        Connection connection = DataBase.getConnection();
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login.getText());
            statement.setString(2, password.getText());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                UserSession.setLoggedInUsername(login.getText());
                loadWindow("users-window.fxml", buttonIn);
            } else {
                displayErrorMessage();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void loadWindow(String fxmlFileName, Button button) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
        Parent parent;
        try {
            parent = loader.load();
            Stage currentStage = (Stage) button.getScene().getWindow();
            currentStage.hide();
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayErrorMessage() {
        information.setText("Неправильный логин или пароль");
    }
}

