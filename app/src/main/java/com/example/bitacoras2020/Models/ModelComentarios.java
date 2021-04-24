package com.example.bitacoras2020.Models;

public class ModelComentarios  {
    String bitacora="", comentario="", usuario="", fecha="", comentarioCreadoPorMi="";

    public ModelComentarios(String bitacora, String comentario, String usuario, String fecha, String comentarioCreadoPorMi) {
        this.bitacora = bitacora;
        this.comentario = comentario;
        this.usuario = usuario;
        this.fecha = fecha;
        this.comentarioCreadoPorMi = comentarioCreadoPorMi;
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

    public String getComentarioCreadoPorMi() {
        return comentarioCreadoPorMi;
    }

    public void setComentarioCreadoPorMi(String sync) {
        this.comentarioCreadoPorMi = sync;
    }
}
