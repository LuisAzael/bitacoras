package com.example.bitacoras2020.Models;

public class ModelArticulosEscaneados {
    String nombre="", serie="", fecha="", sync="", bitacora ="", id="";

    public ModelArticulosEscaneados(String nombre, String serie, String fecha, String sync, String bitacora, String id) {
        this.nombre = nombre;
        this.serie = serie;
        this.fecha = fecha;
        this.sync = sync;
        this.bitacora = bitacora;
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
