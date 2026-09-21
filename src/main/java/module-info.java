module com.astier.bts.client_tcp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.net.http;
    requires com.google.gson;


    opens com.astier.bts.client_tcp to javafx.fxml;
    opens com.astier.bts.client_tcp.modele to com.google.gson;
    exports com.astier.bts.client_tcp;
}