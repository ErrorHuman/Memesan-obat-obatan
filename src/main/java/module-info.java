module com.example.apteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires net.synedra.validatorfx;
    requires javafx.graphics;
    requires java.sql;
    requires org.controlsfx.controls;

    opens com.example.apteca to javafx.fxml;
    exports com.example.apteca;
}