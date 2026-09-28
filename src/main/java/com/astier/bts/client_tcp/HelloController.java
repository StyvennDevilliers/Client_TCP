package com.astier.bts.client_tcp;

import com.astier.bts.client_tcp.modele.Config_Client;
import com.astier.bts.client_tcp.tcp.TCP_Bin;
import com.astier.bts.client_tcp.udp.MulticastDiffusion;
import com.astier.bts.client_tcp.udp.UDP_Text;
import exceptions.DiagnosticException;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    static public TCP_Bin tcp_bin;
    static public UDP_Text udp_text;
    static boolean enRun = false;
    public CheckBox checkbox_UDP;
    public CheckBox checkbox_TCP;
    String adresse, port_TCP,port_UDP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            MulticastDiffusion multicastDiffusion = new MulticastDiffusion();
            Config_Client config = multicastDiffusion.getConf();
            if(config==null){
                TextAreaReponses.appendText("Serveur introuvable (pas de réponse multicast)\n");
                checkbox_TCP.setDisable(true);
                checkbox_UDP.setDisable(true);
                connecter.setDisable(true);
                button.setDisable(true);
            }
            else {
                adresse = config.address();
                port_UDP = String.valueOf(config.port_UDP());
                port_TCP = String.valueOf(config.port_TCP());
            }
        } catch (Exception e) {
            DiagnosticException.afficheException(e);
        }
        voyant.setFill(RED);
        TextFieldPort.setEditable(false);
        TextFieldIP.setEditable(false);
        checkbox_UDP.setOnAction(event -> {
            TextFieldIP.setText(adresse);
            if(checkbox_UDP.isSelected()){
                TextFieldPort.setText(port_UDP);
                connecter.setDisable(true);
                connecter.setVisible(false);
                deconnecter.setDisable(true);
                deconnecter.setVisible(false);
                voyant.setVisible(false);
                checkbox_TCP.setSelected(false);
                try {
                    udp_text = new UDP_Text(InetAddress.getByName(adresse),Integer.parseInt(port_UDP),this);
                } catch (UnknownHostException e) {
                    DiagnosticException.afficheException(e);
                }
                udp_text.connection();
                enRun = true;
            }else{
                connecter.setDisable(false);
                connecter.setVisible(true);
                deconnecter.setDisable(false);
                deconnecter.setVisible(true);
                voyant.setVisible(true);
                try {
                    deconnecter();
                } catch (Exception e) {
                    DiagnosticException.afficheException(e);
                }
                checkbox_UDP.setSelected(false);
            }
        });
        checkbox_TCP.setOnAction(event -> {
            TextFieldIP.setText(adresse);
            if(checkbox_TCP.isSelected()){
                TextFieldPort.setText(port_TCP);
                connecter.setDisable(false);
                connecter.setVisible(true);
                deconnecter.setDisable(false);
                deconnecter.setVisible(true);
                voyant.setVisible(true);
                checkbox_UDP.setSelected(false);
            }
        });
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
            } catch (InterruptedException | IOException e) {
                System.err.println(e.getMessage());
            }
            ;
        });
        button.setOnAction(event -> {
            try {
                envoyer();
            } catch (InterruptedException | IOException e) {
                System.err.println(e.getMessage());
            }
            ;
        });


    }


    private void envoyer() throws InterruptedException, IOException {
        if(!enRun)return;
        if(!checkbox_TCP.isSelected() && !checkbox_UDP.isSelected()) return;
        if(checkbox_TCP.isSelected() && checkbox_UDP.isSelected()) return;

        String requette = TextFieldRequette.getText();
        if(checkbox_TCP.isSelected()){
            tcp_bin.requette(requette);
        }else{
            udp_text.requette(requette);
        }

        if(requette.equalsIgnoreCase("exit")||requette.equalsIgnoreCase("fin")){
            deconnecter();
        }

    }

    private void deconnecter() throws InterruptedException, IOException {
        if(!enRun)return;
        if(checkbox_TCP.isSelected() && checkbox_UDP.isSelected()) return;

        if(checkbox_TCP.isSelected()){
            tcp_bin.deconnection();
        }else{
            udp_text.deconnection();
            connecter.setDisable(false);
            connecter.setVisible(true);
            deconnecter.setDisable(false);
            deconnecter.setVisible(true);
            voyant.setVisible(true);
            checkbox_UDP.setSelected(false);
            TextFieldPort.clear();
            TextFieldIP.clear();
        }


        checkbox_TCP.setDisable(false);
        checkbox_UDP.setDisable(false);
        enRun = false;
    }

    private void connecter() throws IOException {
        if(enRun)return;
        if(!checkbox_TCP.isSelected() && !checkbox_UDP.isSelected()) return;
        if(checkbox_TCP.isSelected() && checkbox_UDP.isSelected()) return;
        if(TextFieldIP.getText().isEmpty() || TextFieldPort.getText().isEmpty()) return;

        tcp_bin = new TCP_Bin(InetAddress.getByName(adresse),Integer.parseInt(port_TCP),this);
        tcp_bin.connection();

        checkbox_TCP.setDisable(true);
        checkbox_UDP.setDisable(true);
        voyant.setFill(GREEN);
        enRun = true;

    }
}