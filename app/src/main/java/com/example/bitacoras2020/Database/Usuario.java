package com.example.bitacoras2020.Database;

import com.orm.SugarRecord;

public class Usuario extends SugarRecord {
    String token="";

    public Usuario() {
    }

    public Usuario(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
