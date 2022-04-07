module com.example.zadanie1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;
    requires lombok;


    opens com.example.zadanie1 to javafx.fxml;
    opens com.example.zadanie1.components to javafx.fxml;
    
    exports com.example.zadanie1;
    exports com.example.zadanie1.components;
}