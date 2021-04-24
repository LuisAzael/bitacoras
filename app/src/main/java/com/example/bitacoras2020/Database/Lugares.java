package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Lugares extends SugarRecord
{
    String nombre ="", status="", latitud="", longitud="", perimetro="", idbase="";

    public Lugares() {
        super();
    }

    public Lugares(String nombre, String status, String latitud, String longitud, String perimetro, String idbase) {
        this.nombre = nombre;
        this.status = status;
        this.latitud = latitud;
        this.longitud = longitud;
        this.perimetro = perimetro;
        this.idbase = idbase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getPerimetro() {
        return perimetro;
    }

    public void setPerimetro(String perimetro) {
        this.perimetro = perimetro;
    }

    public String getIdbase() {
        return idbase;
    }

    public void setIdbase(String idbase) {
        this.idbase = idbase;
    }
}
