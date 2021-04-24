package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Carros extends SugarRecord
{
    String nombre="", status="", idbase="";

    public Carros() {
        super();
    }

    public Carros(String nombre, String status, String idbase) {
        this.nombre = nombre;
        this.status = status;
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

    public String getIdbase() {
        return idbase;
    }

    public void setIdbase(String idbase) {
        this.idbase = idbase;
    }
}
