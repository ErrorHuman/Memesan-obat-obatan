package com.example.apteca;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserWindow {

    @FXML
    private Button ButtonCart;

    @FXML
    private Button ordersPlaced;

    @FXML
    private TableView<Medicine> conclusion;

    @FXML
    private TableColumn<Medicine, BigDecimal> cost;

    @FXML
    private Text information;

    @FXML
    private TextField search;

    @FXML
    private Button placeOrder;

    @FXML
    private TableColumn<Medicine, String> title;

    @FXML
    private TableView<Medicine> cartTable;

    @FXML
    private TableColumn<Medicine, String> cartTitleColumn;

    @FXML
    private TableColumn<Medicine, BigDecimal> cartCostColumn;
    private final ObservableList<Medicine> medicineList = FXCollections.observableArrayList();
    private final ObservableList<Medicine> cartItems = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        placeOrder.setOnAction(event -> openOrderWindow());
        ordersPlaced.setOnAction(event -> buttonOrdersPlaced());
        title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        cost.setCellValueFactory(cellData -> cellData.getValue().costProperty().asObject());
        cartTable.setVisible(false);
        ButtonCart.setOnAction(event -> showCart());
        search.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                searchMedicines(newValue);
            } else {
                medicineList.clear();
                conclusion.setItems(medicineList);
            }
        });
        conclusion.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Medicine selectedMedicine = conclusion.getSelectionModel().getSelectedItem();
                if (selectedMedicine != null) {
                    addToCart(selectedMedicine);
                }
            }
        });
        cartTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Medicine selectedCartItem = cartTable.getSelectionModel().getSelectedItem();
                if (selectedCartItem != null) {
                    removeFromCart(selectedCartItem);
                }
            }
        });

        initializeCartTable();
    }

    @FXML
    void showCart() {
        cartTable.setVisible(!cartTable.isVisible());
    }

    private void searchMedicines(String searchTerm) {
        medicineList.clear();
        Connection connection = DataBase.getConnection();
        String sql = "SELECT id, title, cost FROM medicines WHERE title LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                resultSet.getInt("id");
                String title = resultSet.getString("title");
                BigDecimal cost = resultSet.getBigDecimal("cost");
                Medicine medicine = new Medicine(title, cost);
                medicineList.add(medicine);
            }
            conclusion.setItems(medicineList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayCartItem(Medicine item) {
        if (!cartItems.contains(item)) {
            cartItems.add(item); // Добавление товара если его нет в корзине
            Platform.runLater(() -> cartTable.refresh()); // Обновление отображения таблицы корзины
        }
    }

    private TableColumn<Medicine, Integer> createQuantityColumn() {
        TableColumn<Medicine, Integer> quantityColumn = new TableColumn<>("Кол-во");
        quantityColumn.setPrefWidth(100);
        quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Обработчик изменения количества лекарств в таблице корзины
        quantityColumn.setOnEditCommit(event -> {
            Medicine medicine = event.getRowValue();
            int newQuantity = event.getNewValue();
            medicine.setQuantity(newQuantity);

            // Обновление суммы лекарства при изменении количества
            double newTotalAmount = medicine.getCost() * newQuantity;
            medicine.setTotalAmount(newTotalAmount);
        });

        return quantityColumn;
    }

    private void initializeCartTable() {
        cartTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        cartCostColumn.setCellValueFactory(cellData -> cellData.getValue().costProperty().asObject());

        TableColumn<Medicine, Integer> quantityColumn = createQuantityColumn();

        cartTable.getColumns().add(quantityColumn);
        cartTable.setEditable(true);

        TableColumn<Medicine, Double> totalAmountColumn = new TableColumn<>("Итого");
        totalAmountColumn.setCellValueFactory(cellData -> cellData.getValue().totalAmountProperty().asObject());

        cartTable.getColumns().add(totalAmountColumn);
    }

    private void addToCart(Medicine item) {
        item.setQuantity(0);
        displayCartItem(item);
        refreshCartTable();
    }

    private void removeFromCart(Medicine item) {
        cartItems.remove(item);
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartTable.setItems(cartItems);
        Platform.runLater(() -> cartTable.refresh());
    }

    private void buttonOrdersPlaced() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("orders-placed.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent, 1000, 800);
            Stage currentStage = (Stage) ordersPlaced.getScene().getWindow();
            currentStage.setScene(scene);
            currentStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при переходе на предыдущее окно", e);
        }
    }

    private void openOrderWindow() {
        Connection connection = DataBase.getConnection();
        if (connection != null) {
            String loggedInUsername = UserSession.getLoggedInUsername();
            if (loggedInUsername != null) {
                boolean hasError = false;
                for (Medicine medicine : cartItems) {
                    if (medicine.getQuantity() <= 0) {
                        hasError = true;
                        information.setText("Ошибка: Количество товара '" + medicine.getTitle() + "' должно быть больше 0.");
                        break; // Прерываем цикл при нахождении некорректного товара
                    }
                }

                if (!hasError) {
                    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO orders (login," +
                            " medicines, quantity, total_price) VALUES (?, ?, ?, ?)")) {
                        for (Medicine medicine : cartItems) {
                            statement.setString(1, loggedInUsername);
                            statement.setString(2, medicine.getTitle());
                            statement.setInt(3, medicine.getQuantity());
                            statement.setBigDecimal(4, BigDecimal.valueOf(medicine.getTotalAmount()));
                            statement.addBatch();
                            information.setText("Заказ успешно формлен");
                        }
                        statement.executeBatch();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Logged in username is NULL. Cannot proceed with inserting into database.");
            }
        } else {
            System.out.println("Failed to establish connection to the database.");
        }
    }
}


