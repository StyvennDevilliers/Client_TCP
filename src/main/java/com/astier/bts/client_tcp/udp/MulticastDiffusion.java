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
    private byte ttl = 60;
    private DatagramPacket dp;
    private MulticastSocket ms;
    private final DatagramSocket ds;
    private Config_Client conf;
    private Thread ecoute;

    public MulticastDiffusion() throws IOException {
        ds = new DatagramSocket(portReponse);
        ds.setSoTimeout(3000);

        ms = new MulticastSocket();
        NetworkInterface ni = NetworkInterface.getByName(INTERFACE_NAME);
        if (ni != null) ms.setNetworkInterface(ni);
        ms.setTimeToLive(ttl);
        ms.send(new DatagramPacket(DATA, DATA.length, IA,port));
        ms.close();

        ecoute = new Thread(()->{
                try (ds){
                    dp = new DatagramPacket(dataReponse, dataReponse.length);
                    ds.receive(dp );
                    String message = new String(dp.getData(), 0, dp.getLength(), StandardCharsets.UTF_8).trim();
                    if (!message.isEmpty()) {
                        String[] morceau = message.split(";");
                        conf = new Config_Client(morceau[0], Integer.parseInt(morceau[1]), Integer.parseInt(morceau[2]));
                    }
                } catch (IOException | RuntimeException e) {
                    conf = null;
                }
            });
        ecoute.start();
    }

    public Config_Client getConf() throws InterruptedException {
        ecoute.join(3500); // attend la réponse ou le timeout
        return conf;
    }
}
