module com.astier.bts.client_tcp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;


    opens com.astier.bts.client_tcp to javafx.fxml;
    exports com.astier.bts.client_tcp;
}