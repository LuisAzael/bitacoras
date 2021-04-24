package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Bitacompleta extends SugarRecord {
    String idserver = "", bitacora="", chofer="", ayudante="", carro="", origen="", destino="";

    public Bitacompleta() {
        super();
    }

    public Bitacompleta(String idserver, String bitacora, String chofer, String ayudante, String carro, String origen, String destino) {
        this.idserver = idserver;
        this.bitacora = bitacora;
        this.chofer = chofer;
        this.ayudante = ayudante;
        this.carro = carro;
        this.origen = origen;
        this.destino = destino;
    }


    public String getIdserver() {
        return idserver;
    }

    public void setIdserver(String idserver) {
        this.idserver = idserver;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getAyudante() {
        return ayudante;
    }

    public void setAyudante(String ayudante) {
        this.ayudante = ayudante;
    }

    public String getCarro() {
        return carro;
    }

    public void setCarro(String carro) {
        this.carro = carro;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}
