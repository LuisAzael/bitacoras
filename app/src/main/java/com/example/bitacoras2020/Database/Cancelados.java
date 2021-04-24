package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Cancelados extends SugarRecord {
    String codigo ="", descripcion = "" , serie="",  fecha="", proveedor = "", bitacora="", sync ="", capturado="";



    public Cancelados() {
    }

    public Cancelados(String codigo, String descripcion, String serie, String fecha, String proveedor, String bitacora, String sync, String capturado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.serie = serie;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.bitacora = bitacora;
        this.sync = sync;
        this.capturado = capturado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getCapturado() {
        return capturado;
    }

    public void setCapturado(String capturado) {
        this.capturado = capturado;
    }
}
