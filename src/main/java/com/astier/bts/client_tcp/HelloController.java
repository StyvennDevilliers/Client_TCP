package com.astier.bts.client_tcp;

import com.astier.bts.client_tcp.tcp.TCP;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
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
    static public TCP tcp;
    static boolean enRun = false;
    String adresse,port;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        voyant.setFill(RED);
        //todo
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
       //todo
        if(!enRun)return;
        String requette = TextFieldRequette.getText();
        tcp.requette(requette);
        if(requette.equalsIgnoreCase("exit")||requette.equalsIgnoreCase("fin")){
            deconnecter();
        }

    }

    private void deconnecter() throws InterruptedException, IOException {
        //todo
        if(!enRun)return;
        tcp.deconnection();
        voyant.setFill(RED);
        enRun = false;

    }

    private void connecter() throws IOException {
        //todo
        if(enRun)return;
        adresse = TextFieldIP.getText().trim();
        port =  TextFieldPort.getText().trim();
        tcp = new TCP(InetAddress.getByName(adresse),Integer.parseInt(port),this);
        tcp.connection();
        voyant.setFill(GREEN);
        enRun = true;

    }
}