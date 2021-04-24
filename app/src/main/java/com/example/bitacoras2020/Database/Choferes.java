package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Choferes extends SugarRecord {
    String nombre = "", estatus = "", idbase = "";

    public Choferes() {
        super();
    }

    public Choferes(String nombre, String estatus, String idbase) {
        this.nombre = nombre;
        this.estatus = estatus;
        this.idbase = idbase;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getIdbase() {
        return idbase;
    }

    public void setIdbase(String idbase) {
        this.idbase = idbase;
    }
}
