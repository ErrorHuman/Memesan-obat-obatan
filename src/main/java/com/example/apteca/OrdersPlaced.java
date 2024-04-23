package com.example.apteca;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrdersPlaced {

    @FXML
    private Button checkOrders;


    @FXML
    private Button backButton;

    @FXML
    private TableColumn<Order, String> login;

    @FXML
    private TableColumn<Order, String> medicines;

    @FXML
    private TableColumn<Order, String> orderTime;

    @FXML
    private TableView<Order> ordersPlaced;

    @FXML
    private TableColumn<Order, Integer> quantity;

    @FXML
    private TableColumn<Order, Double> totalPrice;

    @FXML
    void initialize() {
        checkOrders.setOnAction(event ->buttonCheckOrders() );
        backButton.setOnAction(event -> backButton());
    }

    private static List<Order> DataByLoginFromDatabase(String username) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE login = ?";

        try {
            Connection connection = DataBase.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String login = resultSet.getString("login");
                    String medicines = resultSet.getString("medicines");
                    LocalDateTime orderTime = LocalDateTime.parse(resultSet.getString("order_time"),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
                    int quantity = resultSet.getInt("quantity");
                    double totalPrice = resultSet.getDouble("total_price");

                    Order order = new Order(login, medicines, orderTime, quantity, totalPrice);
                    orders.add(order);
                }
            }

            // Повторное подкючение при закрытом соединении
            if (connection.isClosed()) {
                connection = DataBase.getConnection();
                statement = connection.prepareStatement(sql);
                statement.setString(1, username);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String login = resultSet.getString("login");
                        String medicines = resultSet.getString("medicines");
                        LocalDateTime orderTime = LocalDateTime.parse(resultSet.getString("order_time"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
                        int quantity = resultSet.getInt("quantity");
                        double totalPrice = resultSet.getDouble("total_price");

                        Order order = new Order(login, medicines, orderTime, quantity, totalPrice);
                        orders.add(order);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        orders.sort(Comparator.comparing(Order::getOrderTime));

        return orders;
    }

    private void backButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("users-window.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 1000, 800);
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при переходе на предыдущее окно", e);
        }
    }
    private void buttonCheckOrders() {
        String loggedInUsername = UserSession.getLoggedInUsername();
        List<Order> orders = DataByLoginFromDatabase(loggedInUsername);

        // Устанавливаем значения для столбцов таблицы
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        medicines.setCellValueFactory(new PropertyValueFactory<>("medicines"));
        orderTime.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        // Создаем ObservableList и сортируем заказы
        ObservableList<Order> sortedOrders = FXCollections.observableArrayList(orders);
        sortedOrders.sort(Comparator.comparing(Order::getOrderTime));

        // Устанавливаем данные в таблицу
        ordersPlaced.setItems(sortedOrders);
    }
}
