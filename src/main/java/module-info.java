module com.example.womanstorefinal {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    //requires com.mysql.cj;

    opens com.example.womanstorefinal to javafx.fxml;
    exports com.example.womanstorefinal;
}