package com.astier.bts.client_tcp.tcp;

import com.astier.bts.client_tcp.HelloController;
import exceptions.DiagnosticException;
import javafx.application.Platform;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import static javafx.scene.paint.Color.RED;

public class TCP_Text extends Thread{
    int port;
    InetAddress serveur;
    Socket socket;
    boolean marche = false;
    boolean connection = false;
    PrintStream out;
    BufferedReader in;

    HelloController fxmlCont;

    public TCP_Text() {
    }

    public TCP_Text(InetAddress serveur, int port, HelloController fxmlCont) {
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
            socket = new Socket();
            socket.connect(new InetSocketAddress(serveur.getHostName(),port),1000);
            connection= true;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream(),true);
        }catch (Exception e){
            updateMessage(DiagnosticException.afficheException(e));
        }
        marche = true;
        this.start();
    }

    public void deconnection() throws InterruptedException, IOException {
        fxmlCont.voyant.setFill(RED);
        out.println("exit");
        Thread.sleep(1000);
        out.close();
        in.close();
        socket.close();
        marche = false;
    }

    public void requette(String laRequette) throws IOException {
        out.println(laRequette);  // envoi reseau
        System.out.println("la requette " + laRequette);
    }

    public void run() {
        while (marche) {
            char[] buffer = new char[65535];
            byte[] bufferByte = new byte[65535];

            int nblus = 0;
            try {
                nblus = in.read(buffer);

                byte[] bufferByteTemps = new byte[nblus];
                bufferByteTemps= Arrays.copyOf(bufferByte,nblus);
                if (nblus > 0) {
                    String message = new String(bufferByteTemps,0,nblus);

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
