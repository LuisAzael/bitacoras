package com.example.bitacoras2020.Models;

public class ModelBitacorasActivas {
    String bitacora="", origen="", destino="", latitud="",
            longitud="", domicilioRuta="", chofer="",
            ayudante="", carro="", domicilioBitacora="", telefono="", codigoBarras="", tipo="", fecha="";

    public ModelBitacorasActivas(String bitacora, String origen, String destino,
                                 String latitud, String longitud, String domicilioRuta,
                                 String chofer, String ayudante, String carro,
                                 String domicilioBitacora, String telefono, String codigoBarras, String tipo, String fecha) {
        this.bitacora = bitacora;
        this.origen = origen;
        this.destino = destino;
        this.latitud = latitud;
        this.longitud = longitud;
        this.domicilioRuta = domicilioRuta;
        this.chofer = chofer;
        this.ayudante = ayudante;
        this.carro = carro;
        this.domicilioBitacora = domicilioBitacora;
        this.telefono = telefono;
        this.codigoBarras = codigoBarras;
        this.tipo = tipo;
        this.fecha = fecha;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
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

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getDomicilioRuta() {
        return domicilioRuta;
    }

    public void setDomicilioRuta(String domicilioRuta) {
        this.domicilioRuta = domicilioRuta;
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

    public String getDomicilioBitacora() {
        return domicilioBitacora;
    }

    public void setDomicilioBitacora(String domicilioBitacora) {
        this.domicilioBitacora = domicilioBitacora;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
