
//--enable-native-access=javafx.graphics
package com.astier.bts.client_tcp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("hello-view.fxml"));
        stage.setOnCloseRequest((event -> {
            try {
                if (HelloController.enRun){
                    HelloController.tcp_bin.deconnection();
                }
                System.exit(0);
            } catch (Exception ex) {
            }
        }));
        stage.setTitle("TCP-Client  MM");
        stage.getIcons().add(new Image("/icone/index.jpg"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}