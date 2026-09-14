/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.astier.bts.client_tcp.tcp;


import com.astier.bts.client_tcp.HelloController;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


import static javafx.scene.paint.Color.RED;

/**
 * @author Michael
 */
public class TCP extends Thread {
    int port;
    InetAddress serveur;
    Socket socket;
    boolean marche = false;
    boolean connection = false;
    PrintStream out;
    BufferedReader in;
    OutputStream outS;
    InputStream inS;

    HelloController fxmlCont;

    public TCP() {
    }

    public TCP(InetAddress serveur, int port, HelloController fxmlCont) {
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
            if(!serveur.isReachable(500)){
                return;
            }
            socket = new Socket(serveur.getHostName(),port);
            //socket.setSoTimeout(5000);
            connection= true;

            inS = socket.getInputStream();
            outS = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(inS));
            out = new PrintStream(socket.getOutputStream(),true);
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        marche = true;
        this.start();
    }

    public void deconnection() throws InterruptedException, IOException {
        socket.close();
        in.close();
        inS.close();
        out.close();
        outS.close();
        marche = false;
    }

    public void requette(String laRequette) throws IOException {
        //out.println(laRequette);  // envoi reseau
        System.out.println(laRequette.getBytes(StandardCharsets.UTF_8));
        outS.write(laRequette.getBytes(StandardCharsets.UTF_8));
        System.out.println("la requette " + laRequette);
    }

    public void run() {
        while (marche) {
            String message = null;
            char[] buffer = new char[65535];
            byte[] bufferByte = new byte[65535];

            int nblus = 0;
            try {
                //nblus = in.read(buffer);
                nblus=inS.read(bufferByte);

                byte[] bufferByteTemps = new byte[nblus];
                bufferByteTemps=Arrays.copyOf(bufferByte,nblus);
            if (nblus > 0) {
                   //message = new String(buffer, 0, nblus);
                    message = new String(bufferByteTemps,0,nblus);

                    updateMessage(message);
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
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