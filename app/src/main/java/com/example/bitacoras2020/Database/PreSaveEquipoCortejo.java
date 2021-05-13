package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class PreSaveEquipoCortejo extends SugarRecord {
    String bitacora ="", serie="", fecha="", sync="", nombre="", latitud="", longitud="", isBunker="", usuario="";

    public PreSaveEquipoCortejo() {
    }

    public PreSaveEquipoCortejo(String bitacora, String serie, String fecha, String sync, String nombre, String latitud, String longitud, String isBunker, String usuario) {
        this.bitacora = bitacora;
        this.serie = serie;
        this.fecha = fecha;
        this.sync = sync;
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.isBunker = isBunker;
        this.usuario = usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIsBunker() {
        return isBunker;
    }

    public void setIsBunker(String isBunker) {
        this.isBunker = isBunker;
    }
}
