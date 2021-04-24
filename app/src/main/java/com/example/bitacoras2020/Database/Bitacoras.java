package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Bitacoras extends SugarRecord
{
    String idserver="", bitacoras="", nombre="", domicilio="", telefono="", chofer="", ayudante="", carro="", origen="", destino="", status="", destinodomicilio="",
    destinolatitud="", destinolongitud="", ataud="", panteon="", crematorio="", velacion="", cortejo ="", templo="";

    public Bitacoras() {
        super();
    }

    public Bitacoras(String idserver, String bitacoras, String nombre, String domicilio, String telefono, String chofer, String ayudante, String carro, String origen, String destino, String status, String destinodomicilio,
                     String destinolatitud, String destinolongitud, String ataud, String panteon, String crematorio, String velacion, String cortejo, String templo) {
        this.idserver = idserver;
        this.bitacoras = bitacoras;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.telefono = telefono;
        this.chofer = chofer;
        this.ayudante = ayudante;
        this.carro = carro;
        this.origen = origen;
        this.destino = destino;
        this.status = status;
        this.destinodomicilio = destinodomicilio;
        this.destinolatitud = destinolatitud;
        this.destinolongitud = destinolongitud;
        this.ataud = ataud;
        this.panteon = panteon;
        this.crematorio = crematorio;
        this.velacion = velacion;
        this.cortejo = cortejo;
        this.templo = templo;
    }

    public String getIdserver() {
        return idserver;
    }

    public void setIdserver(String idserver) {
        this.idserver = idserver;
    }

    public String getBitacoras() {
        return bitacoras;
    }

    public void setBitacoras(String bitacoras) {
        this.bitacoras = bitacoras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDestinodomicilio() {
        return destinodomicilio;
    }

    public void setDestinodomicilio(String destinodomicilio) {
        this.destinodomicilio = destinodomicilio;
    }

    public String getDestinolatitud() {
        return destinolatitud;
    }

    public void setDestinolatitud(String destinolatitud) {
        this.destinolatitud = destinolatitud;
    }

    public String getDestinolongitud() {
        return destinolongitud;
    }

    public void setDestinolongitud(String destinolongitud) {
        this.destinolongitud = destinolongitud;
    }

    public String getAtaud() {
        return ataud;
    }

    public void setAtaud(String ataud) {
        this.ataud = ataud;
    }

    public String getPanteon() {
        return panteon;
    }

    public void setPanteon(String panteon) {
        this.panteon = panteon;
    }

    public String getCrematorio() {
        return crematorio;
    }

    public void setCrematorio(String crematorio) {
        this.crematorio = crematorio;
    }

    public String getVelacion() {
        return velacion;
    }

    public void setVelacion(String velacion) {
        this.velacion = velacion;
    }

    public String getCortejo() {
        return cortejo;
    }

    public void setCortejo(String cortejo) {
        this.cortejo = cortejo;
    }

    public String getTemplo() {
        return templo;
    }

    public void setTemplo(String templo) {
        this.templo = templo;
    }
}
