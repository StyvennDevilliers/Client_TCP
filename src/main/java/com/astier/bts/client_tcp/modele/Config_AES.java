package com.astier.bts.client_tcp.modele;

public record Config_AES(String mdp,String iv) {
    @Override
    public String mdp() {
        return mdp;
    }

    @Override
    public String iv() {
        return iv;
    }
}
