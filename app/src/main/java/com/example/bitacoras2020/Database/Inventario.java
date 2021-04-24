package com.example.bitacoras2020.Database;

import com.orm.SugarApp;
import com.orm.SugarRecord;

public class Inventario extends SugarRecord {
    String codigo="", descripcion="", serie="", fecha="", proveedor ="", sync="", bitacora="", borrado="", capturado="", latitud="", longitud ="";

    public Inventario() {
    }

    public Inventario(String codigo, String descripcion, String serie, String fecha, String proveedor, String sync, String bitacora, String borrado, String capturado, String latitud, String longitud) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.serie = serie;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.sync = sync;
        this.bitacora = bitacora;
        this.borrado = borrado;
        this.capturado = capturado;
        this.latitud = latitud;
        this.longitud = longitud;
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

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getBitacora() {
        return bitacora;
    }

    public void setBitacora(String bitacora) {
        this.bitacora = bitacora;
    }

    public String getBorrado() {
        return borrado;
    }

    public void setBorrado(String borrado) {
        this.borrado = borrado;
    }

    public String getCapturado() {
        return capturado;
    }

    public void setCapturado(String capturado) {
        this.capturado = capturado;
    }
}
