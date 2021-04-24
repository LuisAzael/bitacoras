package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

import org.json.JSONObject;

import java.util.ArrayList;

public class Adicional extends SugarRecord {
    String horaRecoleccion = "", ropaEntregada = "", lugarDeVelacion ="",
            tipoDeServicio = "", NoEquipoVelacionInstalacion ="", observacionesInstalacion ="",
            NoEquipoVelacionCortejo = "", observacionesCortejo= "", bitacora = "", jsonAdicionalInfo="",  fecha ="", sync="", encapsulado ="", observacionesrecoleccion="", observacionestraslado="",
            procedimiento="", laboratorio="", idLaboratorio="";

    public Adicional() {
    }

    public Adicional(String horaRecoleccion, String ropaEntregada, String lugarDeVelacion,
                     String tipoDeServicio, String noEquipoVelacionInstalacion, String observacionesInstalacion,
                     String noEquipoVelacionCortejo, String observacionesCortejo, String jsonAdicionalInfo, String bitacora, String fecha, String sync, String encapsulado,
                     String observacionesrecoleccion, String observacionestraslado, String procedimiento, String laboratorio, String idLaboratorio) {
        this.horaRecoleccion = horaRecoleccion;
        this.ropaEntregada = ropaEntregada;
        this.lugarDeVelacion = lugarDeVelacion;
        this.tipoDeServicio = tipoDeServicio;
        NoEquipoVelacionInstalacion = noEquipoVelacionInstalacion;
        this.observacionesInstalacion = observacionesInstalacion;
        NoEquipoVelacionCortejo = noEquipoVelacionCortejo;
        this.observacionesCortejo = observacionesCortejo;
        this.jsonAdicionalInfo = jsonAdicionalInfo;
        this.bitacora = bitacora;
        this.fecha = fecha;
        this.sync = sync;
        this.encapsulado = encapsulado;
        this.observacionesrecoleccion = observacionesrecoleccion;
        this.observacionestraslado = observacionestraslado;
        this.procedimiento = procedimiento;
        this.laboratorio = laboratorio;
        this.idLaboratorio = idLaboratorio;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public String getObservacionesrecoleccion() {
        return observacionesrecoleccion;
    }

    public void setObservacionesrecoleccion(String observacionesrecoleccion) {
        this.observacionesrecoleccion = observacionesrecoleccion;
    }

    public String getObservacionestraslado() {
        return observacionestraslado;
    }

    public void setObservacionestraslado(String observacionestraslado) {
        this.observacionestraslado = observacionestraslado;
    }

    public String getHoraRecoleccion() {
        return horaRecoleccion;
    }

    public void setHoraRecoleccion(String horaRecoleccion) {
        this.horaRecoleccion = horaRecoleccion;
    }

    public String getRopaEntregada() {
        return ropaEntregada;
    }

    public void setRopaEntregada(String ropaEntregada) {
        this.ropaEntregada = ropaEntregada;
    }

    public String getLugarDeVelacion() {
        return lugarDeVelacion;
    }

    public void setLugarDeVelacion(String lugarDeVelacion) {
        this.lugarDeVelacion = lugarDeVelacion;
    }

    public String getTipoDeServicio() {
        return tipoDeServicio;
    }

    public void setTipoDeServicio(String tipoDeServicio) {
        this.tipoDeServicio = tipoDeServicio;
    }

    public String getNoEquipoVelacionInstalacion() {
        return NoEquipoVelacionInstalacion;
    }

    public void setNoEquipoVelacionInstalacion(String noEquipoVelacionInstalacion) {
        NoEquipoVelacionInstalacion = noEquipoVelacionInstalacion;
    }

    public String getObservacionesInstalacion() {
        return observacionesInstalacion;
    }

    public void setObservacionesInstalacion(String observacionesInstalacion) {
        this.observacionesInstalacion = observacionesInstalacion;
    }

    public String getNoEquipoVelacionCortejo() {
        return NoEquipoVelacionCortejo;
    }

    public void setNoEquipoVelacionCortejo(String noEquipoVelacionCortejo) {
        NoEquipoVelacionCortejo = noEquipoVelacionCortejo;
    }

    public String getObservacionesCortejo() {
        return observacionesCortejo;
    }

    public void setObservacionesCortejo(String observacionesCortejo) {
        this.observacionesCortejo = observacionesCortejo;
    }

    public String getJsonAdicionalInfo() {
        return jsonAdicionalInfo;
    }

    public void setJsonAdicionalInfo(String jsonAdicionalInfo) {
        this.jsonAdicionalInfo = jsonAdicionalInfo;
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

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }

    public String getEncapsulado() {
        return encapsulado;
    }

    public void setEncapsulado(String encapsulado) {
        this.encapsulado = encapsulado;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getIdLaboratorio() {
        return idLaboratorio;
    }

    public void setIdLaboratorio(String idLaboratorio) {
        this.idLaboratorio = idLaboratorio;
    }
}
