package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Actualzaciones extends SugarRecord {
    String choferes="", lugares="", carros="", activos="", bitacoras="";

    public Actualzaciones() {
        super();
    }

    public Actualzaciones(String choferes, String lugares, String carros, String activos, String bitacoras) {
        this.choferes = choferes;
        this.lugares = lugares;
        this.carros = carros;
        this.activos = activos;
        this.bitacoras = bitacoras;
    }

    public String getChoferes() {
        return choferes;
    }

    public void setChoferes(String choferes) {
        this.choferes = choferes;
    }

    public String getLugares() {
        return lugares;
    }

    public void setLugares(String lugares) {
        this.lugares = lugares;
    }

    public String getCarros() {
        return carros;
    }

    public void setCarros(String carros) {
        this.carros = carros;
    }

    public String getActivos() {
        return activos;
    }

    public void setActivos(String activos) {
        this.activos = activos;
    }

    public String getBitacoras() {
        return bitacoras;
    }

    public void setBitacoras(String bitacoras) {
        this.bitacoras = bitacoras;
    }
}
