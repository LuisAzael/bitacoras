package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class CatalogoArticulos extends SugarRecord {
    String nombre="", letras="";

    public CatalogoArticulos() {
    }

    public CatalogoArticulos(String nombre, String letras) {
        this.nombre = nombre;
        this.letras = letras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getLetras() {
        return letras;
    }

    public void setLetras(String letras) {
        this.letras = letras;
    }
}
