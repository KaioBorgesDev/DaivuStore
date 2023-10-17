module com.example.sprintjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.java;
    requires jbcrypt;
    requires com.auth0.jwt;


    opens com.example.sprintjava to javafx.fxml;
    exports com.example.sprintjava;
}