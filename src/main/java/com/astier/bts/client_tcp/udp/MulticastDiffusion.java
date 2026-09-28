package com.astier.bts.client_tcp.udp;

import com.astier.bts.client_tcp.modele.Config_Client;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MulticastDiffusion {
    private final String INTERFACE_NAME = "ethernet_32769";
    private final InetAddress IA = InetAddress.getByName("224.0.0.250");
    private final byte[] DATA = "Tu es qui?".getBytes(StandardCharsets.UTF_8);
    private byte[] dataReponse= new byte[19];
    private int port = 5555;
    private int portReponse = 5556;
    private byte tts = 60;
    private DatagramPacket dp;
    private MulticastSocket ms;
    private DatagramSocket ds;
    private Config_Client conf;

    public MulticastDiffusion() throws IOException {
        ms = new MulticastSocket();
        NetworkInterface ni = NetworkInterface.getByName(INTERFACE_NAME);
        ms.setNetworkInterface(ni);
        ms.setTimeToLive(60);
        dp = new DatagramPacket(DATA, DATA.length, IA,port);
        ms.send(dp);

        new Thread(()->{
            dp = new DatagramPacket(dataReponse,dataReponse.length);
            try {
                ds = new DatagramSocket(portReponse);
                ds.receive(dp);
                String message = new String(dp.getData());
                if (!message.isEmpty()) {
                    String[] morceau = message.split(";");
                    conf = new Config_Client(morceau[0], Integer.parseInt(morceau[1]), Integer.parseInt(morceau[2]));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public Config_Client getConf() {
        return conf;
    }
}
