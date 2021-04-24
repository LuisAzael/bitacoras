package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Eventos extends SugarRecord
{
    String bitacora="", chofer="", ayudante="", carro="", lugar="", fecha="", hora="", dispositivo="", latitud="", longitud="", estatus="", tipo="", nombre="", domicilio="", telefonos="", destino="",
    automatico="", movimiento ="", usuario="", checkInDriver="";

    public Eventos() {
        super();
    }

    public Eventos(String bitacora, String chofer, String ayudante, String carro,
                   String lugar, String fecha, String hora, String dispositivo,
                   String latitud, String longitud, String estatus, String tipo,
                   String nombre, String domicilio, String telefonos, String destino, String automatico, String movimiento, String usuario, String checkInDriver) {
        this.bitacora = bitacora;
        this.chofer = chofer;
        this.ayudante = ayudante;
        this.carro = carro;
        this.lugar = lugar;
        this.fecha = fecha;
        this.hora = hora;
        this.dispositivo = dispositivo;
        this.dispositivo = dispositivo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estatus = estatus;
        this.tipo = tipo;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.telefonos = telefonos;
        this.destino = destino;
        this.automatico = automatico;
        this.movimiento = movimiento;
        this.usuario = usuario;
        this.checkInDriver = checkInDriver;
    }

    public String getCheckInDriver() {
        return checkInDriver;
    }

    public void setCheckInDriver(String checkInDriver) {
        this.checkInDriver = checkInDriver;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getAutomatico() {
        return automatico;
    }

    public void setAutomatico(String automatico) {
        this.automatico = automatico;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }
}
