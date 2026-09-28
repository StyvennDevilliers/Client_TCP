package com.astier.bts.client_tcp.udp;

import com.astier.bts.client_tcp.HelloController;
import exceptions.DiagnosticException;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static javafx.scene.paint.Color.RED;

public class UDP_Text extends Thread{
    int port;
    InetAddress serveur;
    DatagramSocket socket;
    boolean marche = false;
    boolean connection = false;

    HelloController fxmlCont;

    public UDP_Text() {
    }

    public UDP_Text(InetAddress serveur, int port, HelloController fxmlCont) {
        this.port = port;
        this.serveur = serveur;
        this.fxmlCont = fxmlCont;
        System.out.println("@ serveur: " + serveur + " port: " + port);
    }



    public void connection() {
        if(this.isAlive()){
            return;
        }
        try{
            socket = new DatagramSocket();
            //socket.setSoTimeout(5000);
            connection= true;
            marche = true;
            this.start();
        }catch (Exception e){
            updateMessage(DiagnosticException.afficheException(e));
        }

    }

    public void deconnection() throws InterruptedException, IOException {
        fxmlCont.voyant.setFill(RED);
        byte[] exit = "exit".getBytes(StandardCharsets.UTF_8);
        DatagramPacket paquet = new DatagramPacket(exit, exit.length,serveur,port);
        socket.send(paquet);
        marche = false;
        socket.close();
    }

    public void requette(String laRequette) throws IOException {
        byte[] data = (laRequette + "\n").getBytes(StandardCharsets.UTF_8);
        DatagramPacket paquet = new DatagramPacket(data, data.length,serveur,port);
        socket.send(paquet);  // envoi reseau
        if(laRequette.equalsIgnoreCase("exit")) fxmlCont.deconnecter.fire();
        System.out.println("la requette " + laRequette);

    }

    public void run() {
        while (marche) {
            byte[] bufferByte = new byte[65535];

            try {
                DatagramPacket paquet = new DatagramPacket(bufferByte, bufferByte.length);
                socket.receive(paquet);
                int nblus = paquet.getLength();

                byte[] bufferByteTemps;
                bufferByteTemps= Arrays.copyOf(bufferByte,nblus);
                if (nblus > 0) {
                    String message = new String(bufferByteTemps,0,nblus);
                    System.out.println("Message recu: " + message);
                    updateMessage(message);
                }
            } catch (IOException e) {
                if(marche)updateMessage(DiagnosticException.afficheException(e));
                break;
            }

        }
    }


    /*
    Pour déclencher une opération graphique en dehors du thread graphique  utiliser
    javafx.application.Platform.runLater(java.lang.Runnable)
    Cette méthode permet d'éxécuter le code du runnable par le thread graphique de JavaFX.
    */
    protected void updateMessage(String message) {
        Platform.runLater(() -> fxmlCont.TextAreaReponses.appendText("    MESSAGE SERVEUR >  \n      " + message + "\n"));
    }
}
