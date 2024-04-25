package com.example.apteca;

import java.time.LocalDateTime;

// Класс Order представляет информацию о заказе
public class Order {
    private final String login;
    private final String medicines;
    private final LocalDateTime orderTime;
    private final int quantity;
    private final double totalPrice;

    public Order(String login, String medicines, LocalDateTime orderTime, int quantity, double totalPrice) {
        this.login = login;
        this.medicines = medicines;
        this.orderTime = orderTime;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public String getLogin() {
        return login;
    }

    public String getMedicines() {
        return medicines;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}

