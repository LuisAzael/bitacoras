package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Codigos extends SugarRecord
{
    String bitacora="", fecha="", hora="", codigobarras="";

    public Codigos() {
        super();
    }

    public Codigos(String bitacora, String fecha, String hora, String codigobarras) {
        this.bitacora = bitacora;
        this.fecha = fecha;
        this.hora = hora;
        this.codigobarras = codigobarras;
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

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getCodigobarras() {
        return codigobarras;
    }

    public void setCodigobarras(String codigobarras) {
        this.codigobarras = codigobarras;
    }
}
