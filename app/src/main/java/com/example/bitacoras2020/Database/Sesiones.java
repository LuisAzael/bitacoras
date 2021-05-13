package com.example.bitacoras2020.Database;
import com.orm.SugarRecord;

public class Sesiones extends SugarRecord {
    String usuario="", contrasena="", fecha="", latitud="", longitud="", estatus="", hora="", statos="", isBunker="", isProveedor="", geofence="", isFuneraria="";
    /**
     * estatus 1 = checkin
     * estatus 2 = checkout
     */

    public Sesiones() {
        super();
    }

    public Sesiones(String usuario, String contrasena, String fecha, String latitud,
                    String longitud, String estatus, String hora, String statos, String isBunker, String isProveedor, String geofecne, String isFuneraria) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.estatus = estatus;
        this.hora = hora;
        this.statos = statos;
        this.isBunker = isBunker;
        this.isProveedor = isProveedor;
        this.geofence = geofecne;
        this.isFuneraria = isFuneraria;
    }

    public String getIsFuneraria() {
        return isFuneraria;
    }

    public void setIsFuneraria(String isFuneraria) {
        this.isFuneraria = isFuneraria;
    }

    public String getGeofence() {
        return geofence;
    }

    public void setGeofence(String geofence) {
        this.geofence = geofence;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }


    public String getStatos() {
        return statos;
    }

    public void setStatos(String statos) {
        this.statos = statos;
    }

    public String getIsBunker() {
        return isBunker;
    }

    public void setIsBunker(String isBunker) {
        this.isBunker = isBunker;
    }

    public String getIsProveedor() {
        return isProveedor;
    }

    public void setIsProveedor(String isProveedor) {
        this.isProveedor = isProveedor;
    }
}
