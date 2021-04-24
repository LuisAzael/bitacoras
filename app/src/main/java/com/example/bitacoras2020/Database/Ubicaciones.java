package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Ubicaciones extends SugarRecord
{
    String latitud="", longitud="", fecha="", hora="";

    public Ubicaciones() {
        super();
    }

    public Ubicaciones(String latitud, String longitud, String fecha, String hora) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.fecha = fecha;
        this.hora = hora;
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
}
