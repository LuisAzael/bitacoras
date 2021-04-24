package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Laboratorios extends SugarRecord {
    String nombre="", estatus="", fecha="", idLaboratorio="";

    public Laboratorios() {
    }

    public Laboratorios(String nombre, String estatus, String fecha, String idLaboratorio) {
        this.nombre = nombre;
        this.estatus = estatus;
        this.fecha = fecha;
        this.idLaboratorio = idLaboratorio;
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdLaboratorio() {
        return idLaboratorio;
    }

    public void setIdLaboratorio(String idLaboratorio) {
        this.idLaboratorio = idLaboratorio;
    }
}
