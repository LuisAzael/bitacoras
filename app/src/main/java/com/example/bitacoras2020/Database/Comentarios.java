package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Comentarios extends SugarRecord {
    String bitacora="", comentario="", usuario="", fecha="", sync="", pormi="";

    public Comentarios() {
    }

    public Comentarios(String bitacora, String comentario, String usuario, String fecha, String sync, String pormi) {
        this.bitacora = bitacora;
        this.comentario = comentario;
        this.usuario = usuario;
        this.fecha = fecha;
        this.sync = sync;
        this.pormi = pormi;
    }

    public String getPormi() {
        return pormi;
    }

    public void setPormi(String pormi) {
        this.pormi = pormi;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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
