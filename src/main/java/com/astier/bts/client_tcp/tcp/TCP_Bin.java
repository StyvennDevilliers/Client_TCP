/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astier.bts.client_tcp.tcp;


import aes.Aes_cbc;
import aes.Outils;
import com.astier.bts.client_tcp.HelloController;
import com.astier.bts.client_tcp.config.Lecture_Json;
import com.astier.bts.client_tcp.modele.Config_AES;
import exceptions.DiagnosticException;
import javafx.application.Platform;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


import static javafx.scene.paint.Color.RED;

/**
 * @author Michael
 */
public class TCP_Bin extends Thread {
    int port;
    InetAddress serveur;
    Socket socket;
    boolean marche = false;
    boolean connection = false;
    OutputStream outS;
    InputStream inS;
    Aes_cbc aes;

    HelloController fxmlCont;

    public TCP_Bin() {
    }

    public TCP_Bin(InetAddress serveur,  int port, HelloController fxmlCont) {
        this.port = port;
        this.serveur = serveur;
        this.fxmlCont = fxmlCont;
        System.out.println("@ serveur: " + serveur + " port: " + port);
    }



    public void connection() throws IOException {
        if(this.isAlive()){
            return;
        }
        socket = new Socket();
        socket.connect(new InetSocketAddress(serveur,port),1000);
        //socket.setSoTimeout(5000);
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
        aes = new Aes_cbc(configAes.getMotdepasse(), configAes.getIV());

        inS = socket.getInputStream();
        outS = socket.getOutputStream();
        marche = true;
        this.start();

    }

    public void deconnection() throws InterruptedException, IOException {
        fxmlCont.voyant.setFill(RED);
        byte[] clair = aes.cryptage("exit\n".getBytes(StandardCharsets.UTF_8));
        if (clair == null) updateMessage("Erreur de chiffrement");
        byte[] trame = aes.cryptage("exit\n".getBytes(StandardCharsets.UTF_8));
        if (trame != null) {
            outS.write(trame);
            outS.flush();
        }
        inS.close();
        socket.close();
        marche = false;
    }

    public void requette(String laRequette) throws IOException {
        byte[] trame = aes.cryptage((laRequette + "\n").getBytes(StandardCharsets.UTF_8));
        if (trame == null){
            updateMessage("Erreur de chiffrement");
            return;
        }

        outS.write(trame);
        outS.flush();
        if(laRequette.equalsIgnoreCase("exit")) fxmlCont.deconnecter.fire();
        System.out.println("la requette " + laRequette);
    }

    public void run() {
        while (marche) {
            byte[] bufferByte = new byte[65535];

            try {
                int nblus = inS.read(bufferByte);
                if (nblus == 0) break;
                if (nblus == -1) break;

                byte[] bufferByteTemps=Arrays.copyOf(bufferByte,nblus);
                byte[] clair = aes.decryptage(bufferByteTemps);
                if (clair == null){
                    updateMessage("Erreur de déchiffrement");
                    continue;
                }
                String message = new String(clair, StandardCharsets.UTF_8);
                updateMessage(message);
            } catch (Exception e) {
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