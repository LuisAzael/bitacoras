package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Documentos extends SugarRecord {

    String bitacora ="", documento="", fecha ="", sync="";

    public Documentos() {
    }

    public Documentos(String bitacora, String documento, String fecha, String sync) {
        this.bitacora = bitacora;
        this.documento = documento;
        this.fecha = fecha;
        this.sync = sync;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
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
}
