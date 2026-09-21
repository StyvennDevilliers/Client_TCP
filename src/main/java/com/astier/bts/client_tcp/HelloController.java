package com.astier.bts.client_tcp;

import com.astier.bts.client_tcp.tcp.TCP_Bin;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ResourceBundle;
import static javafx.scene.paint.Color.*;

public class HelloController implements Initializable {
    public Button button;
    public Button connecter;
    public Button deconnecter;
    public TextField TextFieldIP;
    public TextField TextFieldPort;
    public TextField TextFieldRequette;
    public Circle voyant;
    public TextArea TextAreaReponses;
    static public TCP_Bin tcp_bin;
    static boolean enRun = false;
    String adresse,port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voyant.setFill(RED);
        connecter.setOnAction(event -> {
            try {
                connecter();

            } catch (IOException e) {
                System.err.println(e.getMessage());
            };
        });
        deconnecter.setOnAction(event -> {
            try {
                deconnecter();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            };
        });
        button.setOnAction(event -> {
            try {
                envoyer();
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            } catch (IOException e) {
                System.err.println(e.getMessage());
            };
        });


    }


    private void envoyer() throws InterruptedException, IOException {
        if(!enRun)return;
        String requette = TextFieldRequette.getText();
        tcp_bin.requette(requette);
        if(requette.equalsIgnoreCase("exit")||requette.equalsIgnoreCase("fin")){
            deconnecter();
        }

    }

    private void deconnecter() throws InterruptedException, IOException {
        if(!enRun)return;
        tcp_bin.deconnection();
        enRun = false;

    }

    private void connecter() throws IOException {
        if(enRun)return;
        if(TextFieldIP.getText().isEmpty() || TextFieldPort.getText().isEmpty()) return;
        adresse = TextFieldIP.getText().trim();
        port =  TextFieldPort.getText().trim();
        tcp_bin = new TCP_Bin(InetAddress.getByName(adresse),Integer.parseInt(port),this);
        tcp_bin.connection();
        voyant.setFill(GREEN);
        enRun = true;

    }
}