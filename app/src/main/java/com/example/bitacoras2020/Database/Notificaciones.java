package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Notificaciones extends SugarRecord {
    String titulo="", body="", action="", bitacora="", fecha="";

    public Notificaciones() {
    }

    public Notificaciones(String titulo, String body, String action, String bitacora, String fecha) {
        this.titulo = titulo;
        this.body = body;
        this.action = action;
        this.bitacora = bitacora;
        this.fecha = fecha;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
