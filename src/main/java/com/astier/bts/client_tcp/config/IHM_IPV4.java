package com.astier.bts.client_tcp.config;

import com.astier.bts.client_tcp.modele.IPV4;

import java.util.ArrayList;

public class IHM_IPV4 {
    static void main() {
        try{
            ArrayList<IPV4> ipv4s = Outils.getSystemIP();
            ipv4s.forEach(ipv4 -> {
                System.out.println(ipv4);
            });
        }catch (Exception e){

        }
    }
}
