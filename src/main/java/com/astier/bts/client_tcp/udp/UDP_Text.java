package com.astier.bts.client_tcp.udp;

import com.astier.bts.client_tcp.HelloController;
import exceptions.DiagnosticException;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
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


/*
    public void connection() {
        if(this.isAlive()){
            return;
        }
        try{
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            connection= true;
        }catch (Exception e){
            updateMessage(DiagnosticException.afficheException(e));
        }
        marche = true;
        this.start();
    }

    public void deconnection() throws InterruptedException, IOException {
        fxmlCont.voyant.setFill(RED);
        DatagramPacket paquet = new DatagramPacket(buffer, buffer.length,serveur,port);
        socket.send("exit");
        Thread.sleep(1000);
        socket.close();
        marche = false;
    }

    public void requette(String laRequette) throws IOException {
        out.println(laRequette);  // envoi reseau
        System.out.println("la requette " + laRequette);
    }

    public void run() {
        while (marche) {
            String message = null;
            char[] buffer = new char[65535];
            byte[] bufferByte = new byte[65535];

            int nblus = 0;
            try {
                DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
                nblus = in.read(buffer);
                socket.receive(paquet);

                byte[] bufferByteTemps = new byte[nblus];
                bufferByteTemps= Arrays.copyOf(bufferByte,nblus);
                if (nblus > 0) {
                    message = new String(bufferByteTemps,0,nblus);

                    updateMessage(message);
                }
            } catch (IOException e) {
                updateMessage(DiagnosticException.afficheException(e));
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
