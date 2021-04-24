package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Movimientos extends SugarRecord
{
    String idchofer="", idayudante="", androidid="", appversion="", androidmodel="", imei="", fecha="", estatus="", movimiento="", usuario="";

    public Movimientos()
    {
        super();
    }

    public Movimientos(String idchofer, String idayudante, String androidid, String appversion, String androidmodel, String imei, String fecha, String estatus, String movimiento, String usuario) {
        this.idchofer = idchofer;
        this.idayudante = idayudante;
        this.androidid = androidid;
        this.appversion = appversion;
        this.androidmodel = androidmodel;
        this.imei = imei;
        this.fecha = fecha;
        this.estatus = estatus;
        this.movimiento = movimiento;
        this.usuario = usuario;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getIdchofer() {
        return idchofer;
    }

    public void setIdchofer(String idchofer) {
        this.idchofer = idchofer;
    }

    public String getIdayudante() {
        return idayudante;
    }

    public void setIdayudante(String idayudante) {
        this.idayudante = idayudante;
    }

    public String getAndroidid() {
        return androidid;
    }

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public String getAppversion() {
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public String getAndroidmodel() {
        return androidmodel;
    }

    public void setAndroidmodel(String androidmodel) {
        this.androidmodel = androidmodel;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }


    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}

