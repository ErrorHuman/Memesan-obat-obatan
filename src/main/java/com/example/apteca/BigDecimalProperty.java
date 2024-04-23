package com.example.apteca;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.math.BigDecimal;

// Класс BigDecimalProperty расширяет SimpleObjectProperty<BigDecimal>
public class BigDecimalProperty extends SimpleObjectProperty<BigDecimal> {

    public BigDecimalProperty() {
        super();
    }

    // Конструктор с начальным значением
    public BigDecimalProperty(BigDecimal initialValue) {
        super(initialValue);
    }

    // Метод для представления BigDecimal как ObservableValue
    public ObservableValue<BigDecimal> asObject() {
        // Если значение равно null или нулю, возвращаем null
        if (get() == null || get().equals(BigDecimal.ZERO)) {
            return null;
        }
        return this;
    }
}


