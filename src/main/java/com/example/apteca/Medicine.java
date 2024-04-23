package com.example.apteca;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class Medicine {
    private final DoubleProperty totalAmount = new SimpleDoubleProperty();
    private final StringProperty title;
    private final BigDecimalProperty cost;
    private final IntegerProperty quantity;

    public Medicine(String title, BigDecimal cost) {
        this.title = new SimpleStringProperty(title);
        this.cost = new BigDecimalProperty(cost);
        this.quantity = new SimpleIntegerProperty(1); // Инициализация количества
    }

    public double getTotalAmount() {
        return totalAmount.get();
    }

    public void setTotalAmount(double value) {
        totalAmount.set(value);
    }

    public DoubleProperty totalAmountProperty() {
        return totalAmount;
    }

    public double getCost() {
        return cost.get().doubleValue();
    }

    public String getTitle() {
        return title.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public BigDecimalProperty costProperty() {
        return cost;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }
}




