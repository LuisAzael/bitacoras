package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Activos extends SugarRecord
{
    String bitacora="", chofer="", ayudante="",
            carro="", lugar="", fecha="", hora="",
            dispositivo="", latitud="", longitud="",
            estatus="", tipo="", nombre="",
            domicilio="", telefonos="", codigobarras="", destino="", activo ="",
            destinodomicilio="", destinolatitud="", destinolongitud="", movimiento="";


    public Activos() {
        super();
    }

    public Activos(String bitacora, String chofer, String ayudante, String carro, String lugar,
                   String fecha, String hora, String dispositivo, String latitud,
                   String longitud, String estatus, String tipo, String nombre,
                   String domicilio, String telefonos, String codigobarras, String destino, String activo, String destinodomicilio,
                   String destinolatitud, String destinolongitud, String movimiento) {
        this.bitacora = bitacora;
        this.chofer = chofer;
        this.ayudante = ayudante;
        this.carro = carro;
        this.lugar = lugar;
        this.fecha = fecha;
        this.hora = hora;
        this.dispositivo = dispositivo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estatus = estatus;
        this.tipo = tipo;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.telefonos = telefonos;
        this.codigobarras = codigobarras;
        this.destino = destino;
        this.activo = activo;

        this.destinodomicilio = destinodomicilio;
        this.destinolatitud = destinolatitud;
        this.destinolongitud = destinolongitud;
        this.movimiento = movimiento;
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

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
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

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(String telefonos) {
        this.telefonos = telefonos;
    }

    public String getCodigobarras() {
        return codigobarras;
    }

    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
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

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }
}
