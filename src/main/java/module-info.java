module andrea.crud_concesionario {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires okhttp3;
    requires com.squareup.moshi;
    requires com.google.gson;
    requires java.sql;


    opens andrea.crud_concesionario to javafx.fxml, com.google.gson;
    exports andrea.crud_concesionario;
}