/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astier.bts.client_tcp.udp;

import aes.Aes_cbc;
import aes.Outils;
import com.astier.bts.client_tcp.HelloController;
import com.astier.bts.client_tcp.config.Lecture_Json;
import com.astier.bts.client_tcp.modele.Config_AES;
import exceptions.DiagnosticException;
import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static javafx.scene.paint.Color.RED;

/**
 * @author Michael
 */
public class UDP_Bin extends Thread {
    int port;
    InetAddress serveur;
    DatagramSocket socket;
    boolean marche = false;
    boolean connection = false;
    Aes_cbc aes;

    HelloController fxmlCont;

    public UDP_Bin() {
    }

    public UDP_Bin(InetAddress serveur, int port, HelloController fxmlCont) {
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
            socket.setSoTimeout(5000);
            connection= true;
            Lecture_Json lectureJson = new Lecture_Json("src/main/resources/configuration_json.json");
            Config_AES configAes = lectureJson.getConfAES();
            System.out.println("motDePasse = " + configAes.motDePasse());
            System.out.println("iv = " + configAes.iv());
            if (configAes.motDePasse() == null) {
                throw new RuntimeException("motDePasse est null");
            }
            if (configAes.iv() == null) {
                throw new RuntimeException("iv est null");
            }
            aes = new Aes_cbc(Outils.normalizeChaine(configAes.motDePasse(),16), Outils.normalizeChaine(configAes.iv(),16));
        }catch (Exception e){
            updateMessage(DiagnosticException.afficheException(e));
        }
        marche = true;
        this.start();
    }

    public void deconnection() throws InterruptedException, IOException {
        fxmlCont.voyant.setFill(RED);
        byte[] exitCrypt = aes.cryptage("exit".getBytes(StandardCharsets.UTF_8));
        DatagramPacket paquet = new DatagramPacket(exitCrypt, exitCrypt.length,serveur,port);
        socket.send(paquet);
        Thread.sleep(1000);
        socket.close();
        marche = false;
    }

    public void requette(String laRequette) throws IOException {
        DatagramPacket paquet = new DatagramPacket(aes.cryptage((laRequette + "\n").getBytes(StandardCharsets.UTF_8)), laRequette.length(),serveur,port);
        socket.send(paquet);  // envoi reseau
        if(laRequette.equalsIgnoreCase("exit")) fxmlCont.deconnecter.fire();
        marche = false;
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
                    String message = new String(aes.decryptage(bufferByteTemps),0,nblus);

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